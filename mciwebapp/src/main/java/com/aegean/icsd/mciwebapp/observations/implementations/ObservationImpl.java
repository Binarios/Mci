package com.aegean.icsd.mciwebapp.observations.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.object.interfaces.IObservationProvider;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;
import com.aegean.icsd.mciwebapp.observations.dao.IObservationDao;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.observations.interfaces.IObservationSvc;

@Service
public class ObservationImpl implements IObservationSvc {

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
  public Observation createObservation(String playerName, Difficulty difficulty) throws ObservationsException {

    if (difficulty == null
      || StringUtils.isEmpty(playerName)) {
      throw Exceptions.InvalidRequest();
    }

    EntityRules entityRules;
    try {
      entityRules = rules.getGameRules(gameName, difficulty);
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveGameRules(e);
    }

    String lastCompletedLevel = dao.getLastCompletedLevel(difficulty, playerName);
    String newLevel = "" + Integer.parseInt(lastCompletedLevel) + 1;

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
    List<String> chosenWords = new ArrayList<>();

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
        if (obsIds.contains(id)) {
          i--;
        } else {
          obsIds.add(id);
          String word = dao.getAssociatedSubject(id);
          chosenWords.add(word);
          remaining -= nbOfOccurrences;
        }
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

    toCreate.setWords(chosenWords);
    return toCreate;
  }
}
