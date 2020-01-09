package com.aegean.icsd.engine.generator.implementations;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.common.beans.BaseGame;
import com.aegean.icsd.engine.common.beans.BaseGameObject;
import com.aegean.icsd.engine.generator.dao.IGeneratorDao;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRange;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.engine.rules.beans.ValueRangeType;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;
import com.aegean.icsd.ontology.interfaces.IOntologyConnector;

@Service
public class Generator implements IGenerator {
  private static Logger LOGGER = Logger.getLogger(Generator.class);

  @Autowired
  private IRules rules;

  @Autowired
  private IGeneratorDao dao;

  @Autowired
  private IAnnotationReader ano;

  @Autowired
  private IOntologyConnector ont;

  @Autowired
  private IMciModelReader model;

  @Override
  public <GAME_OBJECT extends BaseGameObject> List<GAME_OBJECT> selectGameObject(GAME_OBJECT criteria)
    throws EngineException {
    Map<String, Object> relations = ano.getDataProperties(criteria);
    return (List<GAME_OBJECT>) dao.selectGameObject(relations, criteria.getClass());
  }

  @Override
  public <GAME extends BaseGame> List<GAME> selectGame(GAME criteria) throws EngineException {
    Map<String, Object> relations = ano.getDataProperties(criteria);
    return (List<GAME>) dao.selectGame(relations, criteria.getClass());
  }

  @Override
  public <GAME extends BaseGame> String upsertGame(GAME game) throws EngineException {
    LOGGER.debug("Upserting new Game");
    String entity = ano.getEntityValue(game.getClass());
    String fullName = Utils.getFullGameName(entity, game.getDifficulty());
    return upsertObject(game, fullName);
  }

  @Override
  public <GAME_OBJECT extends BaseGameObject> String upsertGameObject(GAME_OBJECT object) throws EngineException {
    LOGGER.debug("Upserting new Object");
    String entityName = ano.getEntityValue(object.getClass());
    return upsertObject(object, entityName);
  }

  @Override
  public <GAME extends BaseGame, GAME_OBJECT extends BaseGameObject> boolean createObjRelation(
    GAME thisGame,
    GAME_OBJECT thatObj,
    EntityProperty onProperty) throws EngineException {

    String entity = ano.getEntityValue(thisGame.getClass());
    String fullName = Utils.getFullGameName(entity, thisGame.getDifficulty());
    validateProperty(fullName, onProperty);
    return createRelation(thisGame.getId(),  onProperty, thatObj.getId());
  }

  @Override
  public <GAME_OBJECT extends BaseGameObject> boolean createObjRelation(GAME_OBJECT thisObj, GAME_OBJECT thatObj, EntityProperty onProperty)
    throws EngineException {
    String entity = ano.getEntityValue(thisObj.getClass());
    validateProperty(entity, onProperty);
    return createRelation(thisObj.getId(),  onProperty, thatObj.getId());
  }

  @Override
  public int getLastCompletedLevel(String gameName, Difficulty difficulty, String playerName) throws EngineException {
    return dao.getLastCompletedLevel(gameName, difficulty, playerName);
  }

  @Override
  public Long generateLongDataValue(ValueRangeRestriction res) {
    long rangeValue = -1;
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;

    for (ValueRange vRange : res.getRanges()) {
      if (ValueRangeType.EQUALS.equals(vRange.getPredicate())) {
        rangeValue = Integer.parseInt(vRange.getValue());
      } else if (ValueRangeType.MIN.equals(vRange.getPredicate())
        && min > Integer.parseInt(vRange.getValue())) {
        min = Integer.parseInt(vRange.getValue()) + 1;
      } else if (ValueRangeType.MAX.equals(vRange.getPredicate())
        && max < Integer.parseInt(vRange.getValue())) {
        max = Integer.parseInt(vRange.getValue());
      } else if (ValueRangeType.MIN_IN.equals(vRange.getPredicate())
        && min > Integer.parseInt(vRange.getValue())) {
        min = Integer.parseInt(vRange.getValue());
      } else if (ValueRangeType.MAX_IN.equals(vRange.getPredicate())
        && max < Integer.parseInt(vRange.getValue())) {
        max = Integer.parseInt(vRange.getValue()) + 1;
      }
    }

    if (min == Integer.MAX_VALUE && max < Integer.MAX_VALUE ) {
      min = 0;
    }

    if (min > Integer.MIN_VALUE && max < Integer.MAX_VALUE  && rangeValue == -1) {
      if (min == max) {
        rangeValue = min;
      } else if (min < max) {
        rangeValue = ThreadLocalRandom.current().nextInt(min, max);
      }
    }

    return rangeValue;
  }

  void validateProperty(String entity, EntityProperty property) throws EngineException {
    EntityProperty thisObjProp;
    try {
      thisObjProp = rules.getProperty(entity, property.getName());
    } catch (RulesException e) {
      throw GeneratorExceptions.CannotRetrieveRules(entity, e);
    }

    if (thisObjProp == null) {
      throw GeneratorExceptions.CannotCreateRelation("N/A", entity, "N/A",
        String.format("Object of type %s doesn't have property with name %s",
          entity, property.getName()));
    }

    if (!thisObjProp.getRange().equals(property.getRange())) {
      throw GeneratorExceptions.CannotCreateRelation("N/A", entity, "N/A",
        String.format("Property %s has range values of %s. Currently passed values of %s",
          property.getName(), thisObjProp.getRange(), property.getRange()));
    }

  }

  String upsertObject (Object object, String entityName) throws EngineException {
    String id = ano.setEntityId(object);
    String name = ano.getEntityValue(object.getClass());
    LOGGER.info(String.format("Upserting new %s with id %s", name, id));
    Map<String, Object> relations = ano.getDataProperties(object);

    EntityRules er;
    try {
      er = rules.getEntityRules(entityName);
    } catch (RulesException e) {
      throw  GeneratorExceptions.CannotRetrieveRules(entityName, e);
    }

    List<EntityProperty> dataProperties = er.getProperties().stream()
      .filter(x -> !x.isObjectProperty())
      .collect(Collectors.toList());

    boolean success = dao.instantiateObject(id, er.getName());
    if (!success) {
      throw GeneratorExceptions.CannotCreateObject(name);
    }

    for (EntityProperty property : dataProperties) {
      Object rangeValue = relations.get(property.getName());
      if (rangeValue != null) {
        Class<?> rangeClass = model.getJavaClassFromOwlType(property.getRange());
        if (List.class.isAssignableFrom(rangeValue.getClass())) {

          if (property.isMandatory() && ((List)rangeValue).size() > 1) {
            throw GeneratorExceptions.FunctionalRelation(name, property.getName());
          }

          for (Object elem : (List) rangeValue) {
            dao.createValueRelation(id, property.getName(), elem, rangeClass);
          }
        } else {
          dao.createValueRelation(id, property.getName(), rangeValue, rangeClass);
        }
      }
    }
    return id;
  }

  boolean createRelation(String id, EntityProperty onProperty, String objId) throws EngineException {
    LOGGER.info(String.format("Associating %s with %s through the relation %s ", id, objId, onProperty.getName()));
    try {
      if (onProperty.isMandatory() && StringUtils.isEmpty(objId)) {
        throw GeneratorExceptions.CannotCreateRelation(onProperty.getName(), id, objId,
          "Property is marked as mandatory. Relation is missing");
      }

      if (onProperty.isIrreflexive() && id.equals(objId)) {
        throw GeneratorExceptions.CannotCreateRelation(onProperty.getName(), id, objId,
          "Property is marked as irreflexive");
      }

      boolean success = dao.createObjRelation(id, onProperty.getName(), objId);
      if (onProperty.isSymmetric()) {
        LOGGER.info(String.format("Associating %s with %s through the relation %s ", objId, id, onProperty.getName()));
        success &= dao.createObjRelation(objId, onProperty.getName(), id);
      }
      if (!StringUtils.isEmpty(onProperty.getInverse()) && !onProperty.isSymmetric()) {
        LOGGER.info(String.format("Associating %s with %s through the relation %s ", objId, id, onProperty.getInverse()));
        success &= dao.createObjRelation(objId, onProperty.getInverse(), id);
      }
      return success;
    } catch (EngineException e) {
      throw GeneratorExceptions.CannotCreateRelation(onProperty.getName(), id, e);
    }
  }
}
