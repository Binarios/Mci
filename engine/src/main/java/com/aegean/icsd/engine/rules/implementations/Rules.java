package com.aegean.icsd.engine.rules.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRange;
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
    GameRules rules = new GameRules();
    rules.setGameName(gameName);
    rules.setDifficulty(difficulty);

    String entityName = Utils.getFullGameName(gameName, difficulty);
    EntityRules entityRules = getRules(entityName);
    rules.setRestrictions(entityRules.getRestrictions());
    rules.setProperties(entityRules.getProperties());

    return rules;
  }

  @Override
  public EntityRestriction getEntityRestriction(String gameName, Difficulty difficulty, String restrictionName)
    throws RulesException {
    if (StringUtils.isEmpty(gameName)
    || StringUtils.isEmpty(restrictionName)
    || difficulty == null) {
      throw Exceptions.InvalidParameters();
    }

    GameRules rules = getGameRules(gameName, difficulty);
    EntityRestriction restriction = rules.getRestrictions().stream()
      .filter(res -> restrictionName.equals(res.getOnProperty().getName()))
      .findFirst()
      .orElseThrow(() -> Exceptions.CannotFindRestriction(restrictionName, gameName));

    return restriction;
  }

  EntityRules getRules(String entityName) throws RulesException {
    ClassSchema entitySchema;
    try {
      entitySchema = ontology.getClassSchema(entityName);
    } catch (OntologyException e) {
      throw Exceptions.CannotRetrieveClassSchema(entityName, e);
    }

    EntityRules rules = new EntityRules();
    rules.setName(entityName);

    List<EntityRestriction> restrictions = getEntityRestrictions(entitySchema);
    rules.setRestrictions(restrictions);

    List<EntityProperty> properties = getEntityProperties(entitySchema.getProperties());
    rules.setProperties(properties);

    return rules;
  }

  List<EntityProperty> getEntityProperties(List<PropertySchema> availableProperties) {
    List<EntityProperty> properties = new ArrayList<>();
    for (PropertySchema prop : availableProperties) {
      EntityProperty entityProperty = getEntityProperty(prop);
      properties.add(entityProperty);
    }
    return properties;
  }

  EntityProperty getEntityProperty(PropertySchema prop) {
    EntityProperty property = new EntityProperty();
    property.setName(prop.getName());
    property.setRange(prop.getRange());
    property.setObjectProperty(prop.isObjectProperty());
    property.setIrreflexive(prop.isIrreflexive());
    property.setReflexive(prop.isReflexive());
    property.setMandatory(prop.isMandatory());
    property.setSymmetric(prop.isSymmetric());

    return property;
  }

  List<EntityRestriction> getEntityRestrictions(ClassSchema game) {
    List<EntityRestriction> entityRestrictions = new ArrayList<>();

    for(RestrictionSchema res : game.getEqualityRestrictions()) {
      EntityRestriction gameRes = getEntityRestriction(res);
      entityRestrictions.add(gameRes);
    }

    for(RestrictionSchema res : game.getRestrictions()) {
      EntityRestriction gameRes = getEntityRestriction(res);
      entityRestrictions.add(gameRes);
    }

    return entityRestrictions;
  }

  EntityRestriction getEntityRestriction(RestrictionSchema res) {
    EntityRestriction gameRes = new EntityRestriction();
    gameRes.setOnProperty(getEntityProperty(res.getOnPropertySchema()));
    gameRes.setType(RestrictionType.fromString(res.getType()));
    gameRes.setCardinality(getRestrictionCardinality(res));
    gameRes.setDataRange(getDataRanges(res));

    return gameRes;
  }

  ValueRangeRestriction getDataRanges(RestrictionSchema res) {
    ValueRangeRestriction dataRange = new ValueRangeRestriction();

    if (res.getCardinalitySchema() != null) {
      List<DataRangeRestrinctionSchema> dataRestrictions = res.getCardinalitySchema().getDataRangeRestrictions();
      dataRestrictions.stream()
        .collect(Collectors.groupingBy(DataRangeRestrinctionSchema::getDatatype))
        .forEach((dataType, restrictions) -> {
          dataRange.setDataType(dataType);
          List<ValueRange> ranges = new ArrayList<>();
          restrictions.forEach(x -> {
            ValueRange range = new ValueRange();
            range.setPredicate(x.getPredicate());
            range.setValue(x.getValue());
            ranges.add(range);
          });
          dataRange.setRanges(ranges);
        });
    } else if (RestrictionSchema.VALUE_TYPE.equals(res.getType())) {
      ValueRange range = new ValueRange();
      range.setValue(res.getExactValue());
      range.setPredicate("equals");

      List<ValueRange> ranges = new ArrayList<>();
      ranges.add(range);
      dataRange.setRanges(ranges);
      dataRange.setDataType(res.getOnPropertySchema().getRange());
    }
    return dataRange;
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
      default:
        break;
    }

    return cardinality;
  }
}
