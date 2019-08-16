package com.aegean.icsd.mciwebapp.observations.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.object.beans.ObservationObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.interfaces.IImageProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IObservationProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationItem;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;
import com.aegean.icsd.mciwebapp.observations.dao.IObservationDao;
import com.aegean.icsd.mciwebapp.observations.interfaces.IObservationSvc;

@Service
public class ObservationSvc extends AbstractGameSvc<Observation, ObservationResponse> {

  private static Logger LOGGER = Logger.getLogger(ObservationSvc.class);

  @Autowired
  private IObservationDao dao;

  @Autowired
  private IRules rules;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IObservationProvider observationProvider;

  @Autowired
  private IImageProvider imageProvider;

  @Autowired
  private IWordProvider wordProvider;

  @Override
  public ObservationResponse solveGame(String id, String player, Long completionTime,
                                       Map<String, Integer> solution) throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)
      || completionTime == null
      || solution.isEmpty()) {
      throw GameExceptions.InvalidRequest(Observation.NAME);
    }

    Observation obs;
    try {
      obs = generator.getGameWithId(id, player, Observation.class);
    } catch (EngineException e) {
      throw GameExceptions.UnableToRetrieveGame(Observation.NAME, id, player, e);
    }

    if (completionTime > obs.getMaxCompletionTime()) {
      throw GameExceptions.SurpassedMaxCompletionTime(Observation.NAME, id, obs.getMaxCompletionTime());
    }
    if (!StringUtils.isEmpty(obs.getCompletedDate())) {
      throw GameExceptions.GameIsAlreadySolvedAt(Observation.NAME, id, obs.getCompletedDate());
    }

    boolean solved = true;

    for (Map.Entry<String, Integer> subSolution : solution.entrySet()) {
      solved &= dao.solveGame(id, player, subSolution.getKey(), subSolution.getValue());
    }

    if (solved) {
      obs.setCompletedDate(String.valueOf(System.currentTimeMillis()));
      obs.setCompletionTime(completionTime);
      try {
        generator.upsertGame(obs);
      } catch (EngineException e) {
        throw GameExceptions.GenerationError(Observation.NAME, e);
      }
    }

    return toResponse(obs,null, null);
  }

  @Override
  public ObservationResponse createGame(String playerName, Difficulty difficulty) throws MciException {
    LOGGER.info(String.format("Creating Observation game for player %s at the difficulty %s",
      playerName, difficulty.name()));

    if (StringUtils.isEmpty(playerName)) {
      throw GameExceptions.InvalidRequest(Observation.NAME);
    }
    String fullName = Utils.getFullGameName(Observation.NAME, difficulty);

    int lastCompletedLevel;
    try {
      lastCompletedLevel = generator.getLastCompletedLevel(Observation.NAME, difficulty, playerName);
    } catch (EngineException e) {
      throw GameExceptions.FailedToRetrieveLastLevel(Observation.NAME, difficulty, playerName, e);
    }
    int newLevel = lastCompletedLevel + 1;

    EntityRestriction maxCompleteTimeRes;
    try {
      maxCompleteTimeRes = rules.getEntityRestriction(fullName, "maxCompletionTime");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Observation.NAME, e);
    }

    EntityRestriction totalImages;
    try {
      totalImages = rules.getEntityRestriction(fullName, "hasTotalImages");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Observation.NAME, e);
    }

    EntityRestriction hasObservationRes;
    try {
      hasObservationRes = rules.getEntityRestriction(fullName, "hasObservation");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Observation.NAME, e);
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
        ObservationObj obs = observationProvider.getObservation(nbOfOccurrences);
        String path = dao.getImagePath(obs.getId());
        ObservationItem found =  images.stream().filter(x -> x.getImage().equals(path)).findFirst().orElse(null);
        if (obsIds.contains(obs.getId()) || found != null) {
          i--;
        } else {
          obsIds.add(obs.getId());
          ObservationItem item = new ObservationItem();
          item.setImage(path);
          item.setTotalInstances(nbOfOccurrences);
          images.add(item);
        }
        remaining -= nbOfOccurrences;
      } catch (ProviderException e) {
        throw  GameExceptions.GenerationError(Observation.NAME, e);
      }
    }

    try {
      generator.upsertGame(toCreate);
      for (String obsId : obsIds) {
        generator.createObjRelation(toCreate.getId(), hasObservationRes.getOnProperty(), obsId);
      }
    } catch (EngineException e) {
      throw GameExceptions.GenerationError(Observation.NAME, e);
    }

    List<String> chosenWords = dao.getAssociatedSubjects(toCreate.getId());
    return toResponse(toCreate, images, chosenWords);
  }

  ObservationResponse toResponse(Observation obs, List<ObservationItem> images, List<String> words) {
    ObservationResponse resp = new ObservationResponse(obs);
    resp.setSolved(!StringUtils.isEmpty(obs.getCompletedDate()));
    if (images != null) {
      resp.setItems(images);
    }
    if (words != null) {
      resp.setWords(words);
    }
    return resp;
  }

  @Override protected boolean isValid(Object solution) {
    return false;
  }

  @Override protected boolean checkSolution(Observation game, Object solution) {
    return false;
  }

  @Override
  protected Map<EntityRestriction, List<BaseGameObject>> getRestrictions(String fullName, Observation toCreate)
      throws MciException {

    Map<EntityRestriction, List<BaseGameObject>> restrictions = new HashMap<>();

    EntityRestriction totalImages;
    try {
      totalImages = rules.getEntityRestriction(fullName, "hasTotalImages");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Observation.NAME, e);
    }

    EntityRestriction hasObservationRes;
    try {
      hasObservationRes = rules.getEntityRestriction(fullName, "hasObservation");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Observation.NAME, e);
    }

    toCreate.setTotalImages(generator.generateIntDataValue(totalImages.getDataRange()));

    List<BaseGameObject> objects = new ArrayList<>();

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
        ObservationObj criteria = new ObservationObj();
        criteria.setNbOfImages(nbOfOccurrences);
        ObservationObj obs = observationProvider.getNewObservationFor(fullName, criteria);
        objects.add(obs);
        remaining -= nbOfOccurrences;
      } catch (ProviderException e) {
        throw  GameExceptions.GenerationError(Observation.NAME, e);
      }
    }
    restrictions.put(hasObservationRes, objects);

    return restrictions;
  }

  @Override
  protected ObservationResponse toResponse(Observation toCreate) throws MciException {
    ObservationResponse resp = new ObservationResponse(toCreate);
    resp.setSolved(!StringUtils.isEmpty(toCreate.getCompletedDate()));
    List<String> words = dao.getAssociatedSubjects(toCreate.getId());
    List<ObservationItem> images = dao.getObservationItems(toCreate.getId());
    if (images != null) {
      resp.setItems(images);
    }
    if (words != null) {
      resp.setWords(words);
    }
    return resp;
  }
}
