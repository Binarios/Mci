package com.aegean.icsd.engine.generator.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.generator.dao.IGeneratorDao;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.queries.InsertQuery;
import com.aegean.icsd.queries.beans.InsertParam;

@Service
public class Generator implements IGenerator {

  @Autowired
  private IRules rules;

  @Autowired
  private IGeneratorDao dao;

  @Override
  public GameInfo generateGame(String gameName, Difficulty difficulty, String playerName) throws EngineException {
    if(StringUtils.isEmpty(gameName)
      || StringUtils.isEmpty(playerName)
      || difficulty == null) {
      throw Exceptions.InvalidParameters();
    }

    String fullGameName = Utils.getFullGameName(gameName, difficulty);
    GameRules gameRules;
    try {
      gameRules = rules.getGameRules(gameName, difficulty);
    } catch (RulesException e) {
      throw  Exceptions.CannotRetrieveRules(fullGameName, e);
    }
    int latestLevel = dao.getLatestLevel(gameName, playerName);
    String nodeName = dao.generateBasicGame(gameName);

    GameInfo info = new GameInfo();
    info.setId(nodeName);
    info.setPlayerName(playerName);
    info.setMaxCompletionTime("");
    info.setLevel("");

    return info;
  }

  List<InsertQuery> generateRestrictions(String entityNodeName, List<EntityRestriction> restrictions) {

    List<EntityRestriction> copy = new ArrayList<>();
    Collections.copy(copy, restrictions);

    List<EntityRestriction> onlyRestrictions = copy.stream()
      .filter(x -> x.getType().equals(RestrictionType.ONLY))
      .collect(Collectors.toList());

    onlyRestrictions.forEach(onlyRes -> copy.stream()
      .filter(res -> res.getOnProperty().getName().equals(onlyRes.getOnProperty().getName()))
      .forEach(x -> x.getOnProperty().setRange(onlyRes.getOnProperty().getRange()))
    );

    copy.removeAll(onlyRestrictions);

    List<EntityRestriction> copyWithCardinality = calculateCardinality (copy);
    copy.clear();

    List<EntityRestriction> dataTypeRestriction = copyWithCardinality.stream()
      .filter(x -> !x.getOnProperty().isObjectProperty())
      .collect(Collectors.toList());

    List<EntityRestriction> objectRestriction = copyWithCardinality.stream()
      .filter(x -> x.getOnProperty().isObjectProperty())
      .collect(Collectors.toList());

    copyWithCardinality.clear();

    List<InsertQuery> restrictionQueries = new LinkedList<>();

    restrictionQueries.addAll(fulfillDataTypeRestrictions(entityNodeName, dataTypeRestriction));


    return restrictionQueries;
  }

  List<InsertQuery> fulfillDataTypeRestrictions(String entityNodeName, List<EntityRestriction> dataTypeRestrictions) {
    List<InsertQuery> queries = new ArrayList<>();

    for (EntityRestriction res : dataTypeRestrictions) {
      InsertQuery resQuery = fulfillDataTypeRestriction(entityNodeName, res);
      queries.add(resQuery);
    }
    return queries;
  }

  InsertQuery fulfillDataTypeRestriction(String entityNodeName, EntityRestriction res) {
    String predicateVarName = dao.generateNodeName(res.getOnProperty().getName());
    String objectVarName = dao.generateNodeName(res.getOnProperty().getRange());

    String objValue = valueProvider(res);

    InsertParam predicate = dao.constructInsParam(predicateVarName, res.getOnProperty().getName(), true);
    InsertParam object = dao.constructInsParam(objectVarName, objValue, false);
    return new InsertQuery.Builder()
      .forSubject(dao.constructInsParam(entityNodeName, entityNodeName, true))
      .addRelation(predicate, object)
      .build();
  }

  String valueProvider(EntityRestriction res) {
    return null;
  }

  List<EntityRestriction> calculateCardinality(List<EntityRestriction> entityRestrictions) {
    List<EntityRestriction> simplifiedList = new ArrayList<>();

    entityRestrictions.stream()
      .collect(Collectors.groupingBy(x -> x.getOnProperty().getName()))
      .forEach((propertyName, restrictions) ->
        restrictions.forEach( res -> {
          EntityRestriction calculated = simplifiedList.stream()
            .filter(x -> x.getOnProperty().getName().equals(res.getOnProperty().getName())
              && x.getOnProperty().getRange().equals(res.getOnProperty().getRange()))
            .findFirst()
            .orElse(null);

          if (calculated == null) {
            if (res.getType().equals(RestrictionType.EXACTLY)) {
              simplifiedList.add(res);
            } else {
              int cardinality = determineCardinality(res.getOnProperty().getRange(), restrictions);
              res.setCardinality(cardinality);
              simplifiedList.add(res);
            }
          }
        }));
    return simplifiedList;
  }


  int determineCardinality(String rangeClass, List<EntityRestriction> restrictions) {
    int minCardinality = 0;
    int maxCardinality = 0;
    int cardinality = -1;

    List<EntityRestriction> filtered = restrictions.stream()
      .filter(x -> rangeClass.equals(x.getOnProperty().getRange()))
      .collect(Collectors.toList());

    for (EntityRestriction propRestriction : filtered) {
      if (RestrictionType.EXACTLY == propRestriction.getType()) {
        cardinality = propRestriction.getCardinality();
        break;
      } else if (RestrictionType.MIN == propRestriction.getType()) {
        minCardinality = propRestriction.getCardinality();
      } else if (RestrictionType.MAX == propRestriction.getType()) {
        maxCardinality = propRestriction.getCardinality();
      } else if (RestrictionType.SOME == propRestriction.getType()) {
        cardinality = 1;
      }
    }

    if (cardinality == -1) {
      Random random = new Random(System.currentTimeMillis());
      cardinality = random.nextInt((maxCardinality - minCardinality) + 1) + minCardinality;
    }

    return cardinality;
  }

}
