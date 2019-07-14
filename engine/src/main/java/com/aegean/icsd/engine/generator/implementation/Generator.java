package com.aegean.icsd.engine.generator.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
import com.aegean.icsd.engine.providers.interfaces.IValueProvider;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRange;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.queries.InsertQuery;
import com.aegean.icsd.queries.beans.InsertParam;

@Service
public class Generator implements IGenerator {

  @Autowired
  private IRules rules;

  @Autowired
  private IGeneratorDao dao;

  @Autowired
  private IValueProvider provider;

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
      .filter(x -> RestrictionType.ONLY.equals(x.getType()))
      .collect(Collectors.toList());

    onlyRestrictions.forEach(onlyRes -> copy.stream()
      .filter(res -> res.getOnProperty().getName().equals(onlyRes.getOnProperty().getName()))
      .forEach(x -> x.getOnProperty().setRange(onlyRes.getOnProperty().getRange()))
    );

    copy.removeAll(onlyRestrictions);

    List<EntityRestriction> copyWithCardinality = calculateCardinality (copy);
    copy.clear();

    List<EntityRestriction> dataTypeRestrictions = copyWithCardinality.stream()
      .filter(x -> !x.getOnProperty().isObjectProperty())
      .collect(Collectors.toList());

    List<EntityRestriction> objectRestrictions = copyWithCardinality.stream()
      .filter(x -> x.getOnProperty().isObjectProperty())
      .collect(Collectors.toList());

    copyWithCardinality.clear();

    List<InsertQuery> restrictionQueries = new LinkedList<>();

    List<InsertQuery> dataTypeQueries = fulfillDataTypeRestrictions(entityNodeName, dataTypeRestrictions);
    List<InsertQuery> objectQueries = fulfillObjectRestrictions(entityNodeName, objectRestrictions);
    restrictionQueries.addAll(dataTypeQueries);


    return restrictionQueries;
  }

  List<InsertQuery> fulfillObjectRestrictions(String entityNodeName, List<EntityRestriction> objectRestrictions) {
    List<InsertQuery> queries = new LinkedList<>();
    return queries;
  }

  List<InsertQuery> fulfillDataTypeRestrictions(String entityNodeName, List<EntityRestriction> dataTypeRestrictions) {
    List<InsertQuery> queries = new ArrayList<>();

    for (EntityRestriction res : dataTypeRestrictions) {
      List<InsertQuery> resQueries = fulfillDataTypeRestriction(entityNodeName, res);
      queries.addAll(resQueries);
    }
    return queries;
  }

  List<InsertQuery> fulfillDataTypeRestriction(String entityNodeName, EntityRestriction res) {
    List<InsertQuery> queries = new LinkedList<>();
    for (int i = 0; i < res.getCardinality(); i++) {
      InsertQuery q = generateDataTypeInsert(entityNodeName,
        res.getOnProperty().getName(), res.getOnProperty().getRange(), res.getDataRange());
      queries.add(q);
    }

    return queries;
  }

  InsertQuery generateDataTypeInsert(String entityNodeName, String property,
                                     String range, ValueRangeRestriction dataRange) {
    String objValue = calculateDataValue(dataRange);

    String predicateVarName = dao.generateNodeName(property);
    String objectVarName = dao.generateNodeName(range);
    InsertParam predicate = dao.constructInsParam(predicateVarName, property, true);
    InsertParam object = dao.constructInsParam(objectVarName, objValue, false);

    return new InsertQuery.Builder()
      .forSubject(dao.constructInsParam(entityNodeName, entityNodeName, true))
      .addRelation(predicate, object)
      .build();
  }

  String calculateDataValue(ValueRangeRestriction res) {
    String value = "";
    if (res.getDataType().endsWith("integer")) {
      int min = 0;
      int max = 0;

      for (ValueRange range : res.getRanges()) {
        int parsed = Integer.parseInt(range.getValue());
        switch (range.getPredicate()) {
          case "maxExclusive":
            max = parsed - 1;
            break;
          case "maxInclusive":
            max = parsed;
            break;
          case "minExclusive":
            min = parsed + 1;
            break;
          case "minInclusive":
            min = parsed;
            break;
          default:
              break;
        }
      }
      value = String.valueOf(provider.getPositiveValue(min, max));
    }

    return value;
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
            if (RestrictionType.EXACTLY.equals(res.getType())) {
              simplifiedList.add(res);
            } else if (RestrictionType.SOME.equals(res.getType()) || RestrictionType.VALUE.equals(res.getType())) {
              res.setCardinality(1);
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

    List<EntityRestriction> filtered = restrictions.stream()
      .filter(x -> rangeClass.equals(x.getOnProperty().getRange()))
      .collect(Collectors.toList());

    for (EntityRestriction propRestriction : filtered) {
      if (RestrictionType.MIN.equals(propRestriction.getType())) {
        minCardinality = propRestriction.getCardinality();
      } else if (RestrictionType.MAX.equals(propRestriction.getType())) {
        maxCardinality = propRestriction.getCardinality();
      }
    }
    return provider.getPositiveValue(minCardinality, maxCardinality);
  }

}
