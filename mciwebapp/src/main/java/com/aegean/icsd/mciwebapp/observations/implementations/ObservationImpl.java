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
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;
import com.aegean.icsd.mciwebapp.observations.dao.IObservationDao;
import com.aegean.icsd.mciwebapp.providers.beans.ProviderException;
import com.aegean.icsd.mciwebapp.providers.interfaces.IWordProvider;
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

  @Override
  public Observation createObservation(String playerName, Difficulty difficulty) throws ObservationsException {

    if (difficulty == null
      || StringUtils.isEmpty(playerName)) {
      throw Exceptions.InvalidRequest();
    }

    List<EntityRestriction> restrictions;
    try {
      restrictions = rules.getGameRules(gameName, difficulty);
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveGameRules(e);
    }

    List<EntityRestriction> simplifiedList = new ArrayList<>();
    Map<String, List<EntityRestriction>> groupedRestrictions = restrictions.stream()
      .collect(Collectors.groupingBy(x -> x.getOnProperty().getName() + ":" + x.getOnProperty().getRange()));

    int numberOfWords = -1;

    for (Map.Entry<String, List<EntityRestriction>> grp : groupedRestrictions.entrySet()) {
      int cardinality = calculateCardinality(grp.getValue());
      EntityRestriction er = grp.getValue().get(0);
      er.setCardinality(cardinality);
      er.setType(RestrictionType.EXACTLY);
      if ("hasWord".equals(grp.getKey())) {
        numberOfWords = er.getCardinality();
      }
      simplifiedList.add(er);
    }

    String lastCompletedLevel = dao.getLastCompletedLevel(difficulty, playerName);
    int newLevel = Integer.parseInt(lastCompletedLevel) + 1;
    Observation observation = dao.generateCoreGameInstance(playerName, difficulty, newLevel);

    List<String> objIds;
    List<String> words;
    try {
      words = wordProvider.getWords(numberOfWords);
      objIds = createRestrictions(simplifiedList, observation.getId());
    } catch (RulesException | EngineException | ProviderException e) {
      throw Exceptions.GenerationError(e);
    }

    for (EntityRestriction res : simplifiedList) {
      if (!res.getOnProperty().isObjectProperty()) {
        String rangeValue = calculateDataValue(res.getDataRange());
        try {
          generator.createValueRelation(observation.getId(), res.getOnProperty(), rangeValue);
        } catch (EngineException e) {
          throw Exceptions.GenerationError(e);
        }
      }
    }
    return observation;
  }

  List<String> createWordRestriction(List<String> values)
    throws RulesException, EngineException {
    List<String> objIds = new ArrayList<>();
    for (String value : values) {
      String existingId = generator.getObjId("Word", value);
      if (StringUtils.isEmpty(existingId)) {
        objIds.add(existingId);
      } else {
        EntityRules entityRules = rules.getEntityRules("Word");
        List<EntityRestriction> restrictions = entityRules.getRestrictions();


        for (EntityRestriction res : dataRestrictions) {
          String rangeValue = calculateDataValue(res.getDataRange());
          generator.createValueRelation(parentId, res.getOnProperty(), rangeValue);
        }
      }
    }

    return objIds;
  }

  List<String> createRestrictions(List<EntityRestriction> restrictions, String parentId)
    throws RulesException, EngineException {
    List<String> objIds = new ArrayList<>();

    List<EntityRestriction> objRestrictions = restrictions.stream()
      .filter(x -> x.getOnProperty().isObjectProperty())
      .collect(Collectors.toList());

    List<EntityRestriction> dataRestrictions = restrictions.stream()
      .filter(x -> !x.getOnProperty().isObjectProperty())
      .collect(Collectors.toList());

    for (EntityRestriction res : objRestrictions) {
      EntityRules entityRules = rules.getEntityRules(res.getOnProperty().getRange());
      List<EntityRestriction> childRestrictions = entityRules.getRestrictions();
      int cardinality = calculateRestrictionCardinality(res);
      List<String> childrenIds = generator.instantiateObjects(res.getOnProperty().getRange(), cardinality);
      objIds.addAll(childrenIds);

      for (String id : childrenIds) {
        createRestrictions(childRestrictions, id);
        generator.createObjRelation(parentId, res.getOnProperty(), id);
      }
    }

    for (EntityRestriction res : dataRestrictions) {
      String rangeValue = calculateDataValue(res.getDataRange());
      generator.createValueRelation(parentId, res.getOnProperty(), rangeValue);
    }

    return objIds;
  }

  int calculateCardinality(List<EntityRestriction> restrictions) throws ObservationsException {
    int cardinality = calculateMinMaxCardinality(restrictions);
    if (cardinality == -1) {
      EntityRestriction er = restrictions.get(0);
      cardinality = calculateRestrictionCardinality(er);
      if (cardinality == -1) {
        throw Exceptions.CannotCalculateCardinality(er.getOnProperty().getName());
      }
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
