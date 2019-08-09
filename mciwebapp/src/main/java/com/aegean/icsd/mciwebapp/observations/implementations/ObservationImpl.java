package com.aegean.icsd.mciwebapp.observations.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IObservationProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;
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
    toCreate.setMaxCompletionTime(Long.parseLong(calculateDataValue(maxCompleteTimeRes.getDataRange())));
    toCreate.setTotalImages(Integer.parseInt(calculateDataValue(totalImages.getDataRange())));

    List<String> obsIds = new ArrayList<>();

    int remaining = toCreate.getTotalImages();
    for (int i = 0; i < hasObservationRes.getCardinality(); i++) {
      if (remaining < 1) {
        int totalToCreate = ThreadLocalRandom.current().nextInt(0, remaining + 1);
        try {
          String id = observationProvider.getObservationId(totalToCreate);
          obsIds.add(id);
          remaining -= totalToCreate;
        } catch (ProviderException e) {
          throw  Exceptions.GenerationError(e);
        }
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

    //TODO setup the words that the user will see in the Observation bean
    return toCreate;
  }

  String calculateDataValue (ValueRangeRestriction res) {
    String dataType = res.getDataType();
    String rangeValue = null;
    switch (dataType) {
      case "positiveInteger":
        rangeValue = "" + generator.generateIntDataValue(res);
        break;
      case "anyURI":
        break;
      case "string":
        rangeValue = "hello my love!";
        break;
      default:
        if (dataType.contains(";")) {
          String[] possibleValues = dataType.split(";");
          rangeValue = possibleValues[ThreadLocalRandom.current().nextInt(0, possibleValues.length)];
        }
        break;
    }

    return rangeValue;
  }
}
