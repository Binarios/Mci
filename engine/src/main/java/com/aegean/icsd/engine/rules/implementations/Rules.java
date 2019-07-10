package com.aegean.icsd.engine.rules.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.GameProperty;
import com.aegean.icsd.engine.rules.beans.GameRestriction;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.DataRangeRestrinctionSchema;
import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.PropertySchema;
import com.aegean.icsd.ontology.beans.RestrictionSchema;
import com.aegean.icsd.ontology.beans.OntologyException;

@Service
public class Rules implements IRules {

  @Autowired
  private IOntology ontology;

  @Override
  public GameRules getGameRules(String gameName, Difficulty difficulty) throws RulesException {
    ClassSchema game;
    try {
      game = ontology.getClassSchema(Utils.getFullGameName(gameName, difficulty));
    } catch (OntologyException e) {
      throw new RulesException("GGR.1", e.getCodeMsg(), e);
    }

    GameRules rules = new GameRules();
    rules.setGameName(gameName);
    rules.setDifficulty(difficulty);

    List<GameRestriction> restrictions = getGameRestrictions(game);
    rules.setGameRestrictions(restrictions);

    List<GameProperty> properties = getGameProperties(game.getProperties());
    rules.setProperties(properties);

    return rules;
  }

  List<GameProperty> getGameProperties(List<PropertySchema> availableProperties) {
    List<GameProperty> properties = new ArrayList<>();
    for (PropertySchema prop : availableProperties) {
      GameProperty gameProperty = getGameProperty(prop);
      properties.add(gameProperty);
    }
    return properties;
  }

  GameProperty getGameProperty(PropertySchema prop) {
    GameProperty property = new GameProperty();
    property.setName(prop.getName());
    property.setRange(prop.getRange());
    property.setIrreflexive(prop.isIrreflexive());
    property.setReflexive(prop.isReflexive());
    property.setMandatory(prop.isMandatory());
    property.setSymmetric(prop.isSymmetric());

    return property;
  }

  List<GameRestriction> getGameRestrictions(ClassSchema game) {
    List<GameRestriction> gameRestrictions = new ArrayList<>();

    for(RestrictionSchema res : game.getEqualityRestrictions()) {
      GameRestriction gameRes = getGameRestriction(res);
      gameRestrictions.add(gameRes);
    }

    for(RestrictionSchema res : game.getRestrictions()) {
      GameRestriction gameRes = getGameRestriction(res);
      gameRestrictions.add(gameRes);
    }

    Collections.sort(gameRestrictions, (i, k) -> {
      if (i.getType().getOrder() == k.getType().getOrder()) {
        return 0;
      } else if (i.getType().getOrder() > k.getType().getOrder()) {
        return 1;
      } else {
        return -1;
      }
    });

    return gameRestrictions;
  }

  GameRestriction getGameRestriction(RestrictionSchema res) {
    GameRestriction gameRes = new GameRestriction();

    gameRes.setOnProperty(res.getOnPropertySchema().getName());
    gameRes.setRange(res.getOnPropertySchema().getRange());
    gameRes.setType(RestrictionType.fromString(res.getType()));
    gameRes.setCardinality(getRestrictionCardinality(res));
    gameRes.setDataRange(getDataRanges(res));

    return gameRes;
  }

  List<ValueRangeRestriction> getDataRanges(RestrictionSchema res) {
    List<ValueRangeRestriction> dataRanges = new ArrayList<>();

    if (res.getCardinalitySchema() != null) {
      List<DataRangeRestrinctionSchema> dataRestrictions = res.getCardinalitySchema().getDataRangeRestrictions();
      for (DataRangeRestrinctionSchema dataRestriction : dataRestrictions) {
        ValueRangeRestriction valueRange = toValueRangeRestriction(dataRestriction);
        if (valueRange != null) {
          dataRanges.add(valueRange);
        }
      }
    }
    return dataRanges;
  }

  int getRestrictionCardinality(RestrictionSchema res) {
    RestrictionType type = RestrictionType.fromString(res.getType());
    int cardinality = -1;
    switch (type) {
      case EXACTLY:
      case MIN:
      case MAX:
        cardinality = Integer.parseInt(res.getCardinalitySchema().getOccurrence());
        break;
      case VALUE:
        cardinality = Integer.parseInt(res.getExactValue());
        break;
      case ONLY:
      case SOME:
      default:
        break;
    }

    return cardinality;
  }

  ValueRangeRestriction toValueRangeRestriction(DataRangeRestrinctionSchema dataRestriction) {
    if (dataRestriction == null) {
      return  null;
    }

    ValueRangeRestriction res = new ValueRangeRestriction();
    res.setPredicate(dataRestriction.getPredicate());
    res.setDataType(dataRestriction.getDatatype());
    res.setValue(dataRestriction.getValue());

    return res;
  }

}
