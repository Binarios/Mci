package com.aegean.icsd.mciwebapp.observations.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;
import com.aegean.icsd.mciwebapp.observations.dao.IObservationDao;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;
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
  private IWordProvider wordProvider;

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

    List<EntityRestriction> simplifiedRestrictionList = new ArrayList<>();
    Map<String, List<EntityRestriction>> groupedRestrictions = entityRules.getRestrictions()
      .stream()
      .collect(Collectors.groupingBy(x -> x.getOnProperty().getName() + ":" + x.getOnProperty().getRange()));

    int numberOfWords = -1;
    for (Map.Entry<String, List<EntityRestriction>> grp : groupedRestrictions.entrySet()) {
      int cardinality = calculateCardinality(grp.getValue());
      EntityRestriction er = grp.getValue().get(0);
      if (cardinality > 0) {
        er.setCardinality(cardinality);
        er.setType(RestrictionType.EXACTLY);
      }
      String[] keyFragments = grp.getKey().split(":");
      if ("hasWord".equals(keyFragments[0])) {
        numberOfWords = er.getCardinality();
      }
      simplifiedRestrictionList.add(er);
    }

    try {
      generator.upsertObj(toCreate);
    } catch (EngineException e) {
      throw  Exceptions.GenerationError(e);
    }

    List<Word> words;
    try {
      words = wordProvider.getWords(numberOfWords);
    } catch (ProviderException e) {
      throw Exceptions.GenerationError(e);
    }

    return toCreate;
  }

  int calculateCardinality(List<EntityRestriction> restrictions) throws ObservationsException {
    int cardinality = calculateMinMaxCardinality(restrictions);
    if (cardinality == -1) {
      EntityRestriction er = restrictions.get(0);
      cardinality = calculateRestrictionCardinality(er);
    }
    return cardinality;
  }

  int calculateMinMaxCardinality(List<EntityRestriction> restrictions) {
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    int cardinality = -1;

    for (EntityRestriction res : restrictions) {
      if (RestrictionType.MAX.equals(res.getType()) && max < res.getCardinality()) {
        max = res.getCardinality() + 1;
      } else if (RestrictionType.MIN.equals(res.getType()) && min > res.getCardinality()) {
        min = res.getCardinality();
      }
    }

    if (min == Integer.MAX_VALUE && max < Integer.MAX_VALUE) {
      min = 0;
    }

    if (min >= 0) {
      if (min == max) {
        cardinality = min;
      } else if (min < max) {
        cardinality = ThreadLocalRandom.current().nextInt(min, max);
      }
    }

    return cardinality;
  }

  int calculateRestrictionCardinality(EntityRestriction restriction) {
    int minCardinality = Integer.MAX_VALUE;
    int maxCardinality = Integer.MIN_VALUE;
    int cardinality = -1;

    if (RestrictionType.EXACTLY.equals(restriction.getType())) {
      cardinality = restriction.getCardinality();
    } else if (RestrictionType.SOME.equals(restriction.getType())) {
      minCardinality = 4;
      maxCardinality = 6;
    }

    if (cardinality == -1
       && minCardinality < maxCardinality) {
      cardinality = ThreadLocalRandom.current().nextInt(minCardinality, maxCardinality + 1);
    }

    return cardinality;
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
