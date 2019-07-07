package com.aegean.icsd.engine.rules.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.GameProperty;
import com.aegean.icsd.engine.rules.beans.GameRestriction;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.DataRangeRestrinction;
import com.aegean.icsd.ontology.beans.Individual;
import com.aegean.icsd.ontology.beans.IndividualProperty;
import com.aegean.icsd.ontology.beans.IndividualRestriction;
import com.aegean.icsd.ontology.beans.OntologyException;

@Service
public class Rules implements IRules {

  @Autowired
  private IOntology ontology;

  @Override
  public GameRules getGameRules(String gameName, String difficulty) throws RulesException {
    Individual game;
    try {
      game = ontology.generateIndividual(generateGameName(gameName, difficulty));
    } catch (OntologyException e) {
      throw new RulesException("GGR.1", e.getCodeMsg(), e);
    }

    GameRules rules = new GameRules();
    rules.setGameName(gameName);
    rules.setDifficulty(difficulty);

    List<GameRestriction> restrictions = generateGameRestrictions(game);
    rules.setGameRestrictions(restrictions);

    List<GameProperty> properties = generateGameProperties(game.getProperties());
    rules.setProperties(properties);

    return rules;
  }

  List<GameProperty> generateGameProperties(List<IndividualProperty> availableProperties) {
    List<GameProperty> properties = new ArrayList<>();
    for (IndividualProperty prop : availableProperties) {
      GameProperty gameProperty = generateGameProperty(prop);
      properties.add(gameProperty);
    }
    return properties;
  }

  GameProperty generateGameProperty(IndividualProperty prop) {
    GameProperty property = new GameProperty();
    property.setName(prop.getName());
    property.setRange(prop.getRange());
    property.setIrreflexive(prop.isIrreflexive());
    property.setReflexive(prop.isReflexive());
    property.setMandatory(prop.isMandatory());
    property.setSymmetric(prop.isSymmetric());

    return property;
  }

  List<GameRestriction> generateGameRestrictions(Individual game) {
    List<GameRestriction> gameRestrictions = new ArrayList<>();

    for(IndividualRestriction res : game.getEqualityRestrictions()) {
      GameRestriction gameRes = generateGameRestriction(res);
      gameRestrictions.add(gameRes);
    }

    for(IndividualRestriction res : game.getRestrictions()) {
      GameRestriction gameRes = generateGameRestriction(res);
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

  GameRestriction generateGameRestriction(IndividualRestriction res) {
    GameRestriction gameRes = new GameRestriction();

    gameRes.setOnProperty(res.getOnIndividualProperty().getName());
    gameRes.setRange(res.getOnIndividualProperty().getRange());
    gameRes.setType(RestrictionType.fromString(res.getType()));
    gameRes.setCardinality(getRestrictionCardinality(res));
    gameRes.setDataRange(getDataRanges(res));

    return gameRes;
  }

  List<ValueRangeRestriction> getDataRanges(IndividualRestriction res) {
    List<ValueRangeRestriction> dataRanges = new ArrayList<>();

    if (res.getCardinality() != null) {
      List<DataRangeRestrinction> dataRestrictions = res.getCardinality().getDataRangeRestrictions();
      for (DataRangeRestrinction dataRestriction : dataRestrictions) {
        ValueRangeRestriction valueRange = toValueRangeRestriction(dataRestriction);
        if (valueRange != null) {
          dataRanges.add(valueRange);
        }
      }
    }
    return dataRanges;
  }

  int getRestrictionCardinality(IndividualRestriction res) {
    RestrictionType type = RestrictionType.fromString(res.getType());
    int cardinality = -1;
    switch (type) {
      case EXACTLY:
      case MIN:
      case MAX:
        cardinality = Integer.parseInt(res.getCardinality().getOccurrence());
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

  ValueRangeRestriction toValueRangeRestriction(DataRangeRestrinction dataRestriction) {
    if (dataRestriction == null) {
      return  null;
    }

    ValueRangeRestriction res = new ValueRangeRestriction();
    res.setPredicate(dataRestriction.getPredicate());
    res.setDataType(dataRestriction.getDatatype());
    res.setValue(dataRestriction.getValue());

    return res;
  }

  String generateGameName(String gameName, String difficulty) {
    return  difficulty + gameName;
  }
}
