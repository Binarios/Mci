package com.aegean.icsd.engine.rules.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.rules.beans.CardinalityType;
import com.aegean.icsd.engine.rules.beans.GameProperty;
import com.aegean.icsd.engine.rules.beans.GameRestriction;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RestrictionCardinality;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.ontology.IOntology;
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
    return gameRestrictions;
  }

  GameRestriction generateGameRestriction(IndividualRestriction res) {
    GameRestriction gameRes = new GameRestriction();

    gameRes.setOnProperty(res.getOnIndividualProperty().getName());
    gameRes.setRange(res.getOnIndividualProperty().getRange());
    RestrictionCardinality cardinality = generateCardinalityRule(res);
    gameRes.setRestrictionCardinality(cardinality);
    gameRes.setOrder(cardinality.getType().getOrder());

    return gameRes;
  }

  RestrictionCardinality generateCardinalityRule(IndividualRestriction res) {
    RestrictionCardinality rule = new RestrictionCardinality();
    CardinalityType type = CardinalityType.fromString(res.getType());
    rule.setType(type);
    switch (type) {
      case VALUE:
        rule.setValue(Integer.parseInt(res.getExactValue()));
        break;
      case EXACTLY:
      case MAX:
      case MIN:
        rule.setValue(Integer.parseInt(res.getCardinality().getOccurrence()));
        break;
      case ONLY:
      default:
        rule.setValue(-1);
        break;
    }

    return rule;
  }

  String generateGameName(String gameName, String difficulty) {
    return  difficulty + gameName;
  }
}
