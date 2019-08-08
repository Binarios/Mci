package com.aegean.icsd.mciwebapp.observations.implementations;

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
  private IObjectProvider wordProvider;

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

    Observation toCreate = new Observation();
    toCreate.setPlayerName(playerName);
    toCreate.setLevel(newLevel);
    toCreate.setDifficulty(difficulty);
    toCreate.setMaxCompletionTime(Long.parseLong(calculateDataValue(maxCompleteTimeRes.getDataRange())));

    List<EntityRestriction> simplifiedRestrictionList =  generator.calculateExactCardinality(entityRules.getRestrictions());

    int numberOfWords = -1;
    for (EntityRestriction res : simplifiedRestrictionList) {
      if ("hasWord".equals(res.getOnProperty().getName())) {
        numberOfWords = res.getCardinality();
      }
    }

    try {
      generator.upsertObj(toCreate);
    } catch (EngineException e) {
      throw  Exceptions.GenerationError(e);
    }

    List<String> wordsIds;
    try {
      wordsIds = wordProvider.getObjectsIds(Word.NAME, numberOfWords);
    } catch (ProviderException e) {
      throw Exceptions.GenerationError(e);
    }

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
