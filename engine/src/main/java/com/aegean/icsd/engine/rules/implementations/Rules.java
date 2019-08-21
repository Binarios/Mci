package com.aegean.icsd.engine.rules.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRange;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.engine.rules.beans.ValueRangeType;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.DataRangeRestrinctionSchema;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.beans.PropertySchema;
import com.aegean.icsd.ontology.beans.RestrictionSchema;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;

@Service
public class Rules implements IRules {
  private static Logger LOGGER = Logger.getLogger(Rules.class);

  @Autowired
  private IMciModelReader model;

  @Override
  public EntityRules getGameRules(String gameName, Difficulty difficulty) throws RulesException {
    String entityName = Utils.getFullGameName(gameName, difficulty);
    return getEntityRules(entityName);
  }


  @Override
  public EntityRestriction getEntityRestriction(String entityName, String restrictionName) throws RulesException {
    LOGGER.info(String.format("Retrieving restriction %s of %s", restrictionName, entityName));
    EntityRules rules = getEntityRules(entityName);
    EntityRestriction er;

    List<EntityRestriction> restrictions = rules.getRestrictions().stream()
      .filter(x -> restrictionName.equals(x.getOnProperty().getName()))
      .collect(Collectors.toList());

    EntityRestriction only = restrictions.stream()
      .filter(r -> RestrictionType.ONLY.equals(r.getType()))
      .findFirst()
      .orElse(null);

    EntityRestriction value = restrictions.stream()
      .filter(r -> RestrictionType.VALUE.equals(r.getType()))
      .findFirst()
      .orElse(null);

    EntityRestriction exactly = restrictions.stream()
      .filter(r -> RestrictionType.EXACTLY.equals(r.getType()))
      .findFirst()
      .orElse(null);

    EntityRestriction min = restrictions.stream()
      .filter(r -> RestrictionType.MIN.equals(r.getType()))
      .findFirst()
      .orElse(null);

    EntityRestriction max = restrictions.stream()
      .filter(r -> RestrictionType.MAX.equals(r.getType()))
      .findFirst()
      .orElse(null);

    EntityRestriction some = restrictions.stream()
      .filter(r -> RestrictionType.SOME.equals(r.getType()))
      .findFirst()
      .orElse(null);

    if (value != null) {
      er = overrideRange(value, only);
    } else if (exactly != null) {
      er = overrideRange(exactly, only);
    } else if (min != null || max != null) {
      er = calculateMinMax(min, max);
      er = overrideRange(er, only);
    } else {
      er = some;
    }

    if (er != null && er.getOnProperty() != null) {
      String parentName = er.getOnProperty().getParent();
      if (parentName != null) {
        EntityRestriction parentOverride = rules.getRestrictions().stream()
          .filter(x -> parentName.equals(x.getOnProperty().getName()) && RestrictionType.ONLY.equals(x.getType()))
          .findFirst()
          .orElse(null);

        if (parentOverride != null  && only == null) {
          er = overrideRange(er, parentOverride);
        }
      }
    }

    return er;
  }

  EntityRestriction overrideRange(EntityRestriction er, EntityRestriction only) {
    if (only != null) {
      er.setDataRange(only.getDataRange());
    }
    return er;
  }

  EntityRestriction calculateMinMax(EntityRestriction minRes, EntityRestriction maxRes) {

    EntityRestriction toUse = minRes != null ? minRes : maxRes;

    if (toUse != null) {
      int min = minRes == null ? 1 : minRes.getCardinality();
      int max = maxRes == null ? min : maxRes.getCardinality();

      // +1 is for inclusive
      int cardinality = ThreadLocalRandom.current().nextInt(min, max + 1);
      toUse.setType(RestrictionType.EXACTLY);
      toUse.setCardinality(cardinality);
    }
    return toUse;
  }

  @Override
  public EntityRules getEntityRules(String entityName) throws RulesException {
    LOGGER.info(String.format("Retrieving rules for %s", entityName));

    ClassSchema entitySchema;
    try {
      entitySchema = model.getClassSchema(entityName);
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
    property.setParent(prop.getParent());
    property.setInverse(prop.getInverse());
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
            range.setPredicate(ValueRangeType.fromString(x.getPredicate()));
            range.setValue(x.getValue());
            ranges.add(range);
          });
          dataRange.setRanges(ranges);
        });
    } else if (RestrictionSchema.VALUE_TYPE.equals(res.getType())) {
      ValueRange range = new ValueRange();
      range.setValue(res.getExactValue());
      range.setPredicate(ValueRangeType.EQUALS);

      List<ValueRange> ranges = new ArrayList<>();
      ranges.add(range);
      dataRange.setRanges(ranges);
    }
    dataRange.setDataType(res.getOnPropertySchema().getRange());
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
