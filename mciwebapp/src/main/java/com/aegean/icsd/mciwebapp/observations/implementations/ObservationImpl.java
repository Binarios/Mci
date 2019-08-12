package com.aegean.icsd.mciwebapp.observations.implementations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.object.interfaces.IObservationProvider;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationItem;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;
import com.aegean.icsd.mciwebapp.observations.dao.IObservationDao;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.observations.interfaces.IObservationSvc;

@Service
public class ObservationImpl implements IObservationSvc {

  private static Logger LOGGER = Logger.getLogger(ObservationImpl.class);
  private final String gameName = "Observation";

  @Autowired
  private IObservationDao dao;

  @Autowired
  private IRules rules;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IObservationProvider observationProvider;

  @Autowired
  private IAnnotationReader ano;

  @Override
  public List<ObservationResponse> getObservations(String playerName) throws MciException {
    if (StringUtils.isEmpty(playerName)) {
      throw Exceptions.InvalidRequest();
    }

    List<Observation> observations = null;
    try {
      observations = generator.getGamesForPlayer(gameName, playerName, Observation.class);
    } catch (EngineException e) {
      throw Exceptions.FailedToRetrieveGames(playerName, e);
    }

    List<ObservationResponse> res = new ArrayList<>();
    for (Observation observation : observations) {
      res.add(toResponse(observation, null, null));
    }
    return res;
  }

  @Override
  public ObservationResponse getObservation(String id, String player) throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)) {
      throw Exceptions.InvalidRequest();
    }

    Observation obs = dao.getById(id, player);
    if (obs == null) {
      throw Exceptions.UnableToRetrieveGame(id, player);
    }

    List<String> chosenWords = dao.getAssociatedSubjects(obs.getId());
    List<ObservationItem> images = dao.getObservationItems(id);

    return toResponse(obs, images, chosenWords);
  }

  @Override
  public ObservationResponse solveGame(String id, String player, Long completionTime,
                                       Map<String, Integer> solution) throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)
      || completionTime == null
      || solution.isEmpty()) {
      throw Exceptions.InvalidRequest();
    }

    Observation obs = dao.getById(id, player);
    if (obs == null) {
      throw Exceptions.UnableToRetrieveGame(id, player);
    }
    if (completionTime > obs.getMaxCompletionTime()) {
      throw Exceptions.SurpassedMaxCompletionTime(id, obs.getMaxCompletionTime());
    }
    if (!StringUtils.isEmpty(obs.getCompletedDate())) {
      throw Exceptions.GameIsAlreadySolvedAt(id, obs.getCompletedDate());
    }

    boolean solved = true;

    for (Map.Entry<String, Integer> subSolution : solution.entrySet()) {
      solved &= dao.solveGame(id, player, subSolution.getKey(), subSolution.getValue());
    }

    if (solved) {
      obs.setCompletedDate(String.valueOf(System.currentTimeMillis()));
      obs.setCompletionTime(completionTime);
      try {
        generator.upsertObj(obs);
      } catch (EngineException e) {
        throw  Exceptions.GenerationError(e);
      }
    }

    return toResponse(obs,null, null);
  }


  @Override
  public ObservationResponse createObservation(String playerName, Difficulty difficulty) throws MciException {
    LOGGER.info(String.format("Creating Observation game for player %s at the difficulty %s",
      playerName, difficulty.name()));

    if (StringUtils.isEmpty(playerName)) {
      throw Exceptions.InvalidRequest();
    }

    EntityRules entityRules;
    try {
      entityRules = rules.getGameRules(gameName, difficulty);
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveGameRules(e);
    }

    int lastCompletedLevel;
    try {
      lastCompletedLevel = generator.getLastCompletedLevel(gameName, difficulty, playerName);
    } catch (EngineException e) {
      throw Exceptions.FailedToRetrieveLastLevel(gameName, difficulty, playerName, e);
    }
    int newLevel = lastCompletedLevel + 1;

    EntityRestriction maxCompleteTimeRes;
    try {
      maxCompleteTimeRes = rules.getEntityRestriction(entityRules.getName(), "maxCompletionTime");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveGameRules(e);
    }

    EntityRestriction totalImages;
    try {
      totalImages = rules.getEntityRestriction(entityRules.getName(), "hasTotalImages");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveGameRules(e);
    }

    EntityRestriction hasObservationRes;
    try {
      hasObservationRes = rules.getEntityRestriction(entityRules.getName(), "hasObservation");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveGameRules(e);
    }

    Observation toCreate = new Observation();
    toCreate.setPlayerName(playerName);
    toCreate.setLevel(newLevel);
    toCreate.setDifficulty(difficulty);
    toCreate.setMaxCompletionTime(Long.parseLong("" + generator.generateIntDataValue(maxCompleteTimeRes.getDataRange())));
    toCreate.setTotalImages(generator.generateIntDataValue(totalImages.getDataRange()));

    List<String> obsIds = new ArrayList<>();
    List<ObservationItem> images = new ArrayList<>();

    int remaining = toCreate.getTotalImages();
    for (int i = 0; i < hasObservationRes.getCardinality(); i++) {
      int nbOfOccurrences;
      if (remaining < 1) {
        nbOfOccurrences = 0;
      } else if (i + 1 >= hasObservationRes.getCardinality()) {
        nbOfOccurrences = remaining;
      } else {
        nbOfOccurrences = ThreadLocalRandom.current().nextInt(0, remaining + 1);
      }
      try {
        String id = observationProvider.getObservationId(nbOfOccurrences);
        String path = dao.getImagePath(id);
        ObservationItem found =  images.stream().filter(x -> x.getImage().equals(path)).findFirst().orElse(null);
        if (obsIds.contains(id) || found != null) {
          i--;
        } else {
          obsIds.add(id);
          ObservationItem item = new ObservationItem();
          item.setImage(path);
          item.setTotalInstances(nbOfOccurrences);
          images.add(item);
        }
        remaining -= nbOfOccurrences;
      } catch (ProviderException e) {
        throw  Exceptions.GenerationError(e);
      }
    }

    try {
      generator.upsertObj(toCreate);
      for (String obsId : obsIds) {
        generator.createObjRelation(toCreate.getId(), hasObservationRes.getOnProperty(), obsId);
      }
    } catch (EngineException e) {
      throw  Exceptions.GenerationError(e);
    }

    List<String> chosenWords = dao.getAssociatedSubjects(toCreate.getId());
    ObservationResponse response = toResponse(toCreate, images, chosenWords);

    return response;
  }

  ObservationResponse toResponse(Observation obs, List<ObservationItem> images, List<String> words) {
    ObservationResponse resp = new ObservationResponse();
    resp.setSolved(!StringUtils.isEmpty(obs.getCompletedDate()));
    resp.setObservation(obs);
    if (images != null) {
      resp.setItems(images);
    }
    if (words != null) {
      resp.setWords(words);
    }
    return resp;
  }
}
