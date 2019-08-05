package com.aegean.icsd.engine.generator.implementations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.annotations.Entity;
import com.aegean.icsd.engine.annotations.Key;
import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.generator.dao.IGeneratorDao;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRange;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.engine.rules.beans.ValueRangeType;
import com.aegean.icsd.engine.rules.interfaces.IRules;

@Service
public class Generator implements IGenerator {

  @Autowired
  private IRules rules;

  @Autowired
  private IGeneratorDao dao;

  @Override
  public GameInfo instantiateGame(GameInfo info) throws EngineException {
    if (info == null) {
      throw Exceptions.InvalidParameters();
    }
    String gameId = generateGameId(info.getPlayerName(), info.getGameName(), info.getDifficulty(), info.getLevel());
    info.setId(gameId);
    try {
      boolean success = dao.generateBasicGame(info);
      return success ? info : null ;
    } catch (EngineException e) {
      throw Exceptions.CannotCreateCoreGame(gameId, e);
    }
  }

  @Override
  public GameInfo getLastGeneratedIndividual(String gameName, Difficulty difficulty, String playerName) {
    return null;
  }

  @Override
  public boolean createValueRelation(String id, EntityProperty onProperty, String rangeValue) throws EngineException {
    try {
      boolean success = dao.createValueRelation(id, onProperty.getName(), rangeValue);
      return success;
    } catch (EngineException e) {
      throw Exceptions.CannotCreateRelation(onProperty.getName(), id, e);
    }
  }

  @Override
  public boolean createObjRelation(String id, EntityProperty onProperty, String objId) throws EngineException {
    try {
      boolean success = dao.createObjRelation(id, onProperty.getName(), objId);
      return success;
    } catch (EngineException e) {
      throw Exceptions.CannotCreateRelation(onProperty.getName(), id, e);
    }
  }

  @Override
  public List<String> instantiateObjects(String objectType, int cardinality) throws EngineException {
    if (StringUtils.isEmpty(objectType)) {
      throw Exceptions.InvalidParameters();
    }
    List<String> ids = new ArrayList<>();
    for (int i = 0; i < cardinality; i++) {
      ids.add(instantiateObject(objectType));
    }
    return ids;
  }

  @Override
  public String instantiateObject(String objectType) throws EngineException {
    if (StringUtils.isEmpty(objectType)) {
      throw Exceptions.InvalidParameters();
    }

    String id = generateObjectId(objectType);
    boolean success = dao.instantiateObject(id, objectType);
    if (!success) {
      throw Exceptions.CannotCreateObject(objectType);
    }
    return id;
  }

  @Override
  public int generateIntDataValue(ValueRangeRestriction res) {
    int rangeValue = -1;
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

  @Override
  public boolean upsertObj(Object object) throws EngineException {
    String id = getObjectId(object);
    String name = getObjectType(object);

    boolean exists = dao.isCreated(id);

    if(!exists) {
      EntityRules eR;
      try {
        eR = rules.getEntityRules(name);
      } catch (RulesException e) {
        throw Exceptions.CannotRetrieveRules(name, e);
      }



    }
    return true;
  }

  String getObjectId(Object object) throws EngineException {
    String objName = getObjectType(object);
    String id = null;
    for (Field field : object.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(Key.class)) {
        try {
          id = (String) field.get(object);
          break;
        } catch (IllegalAccessException e) {
          throw Exceptions.GenericError(e);
        }
      }
    }
    if (id == null) {
      throw Exceptions.UnableToReadAnnotation(Key.class.getSimpleName());
    }
    return objName + "_" + id;
  }

  String getObjectType(Object object) throws EngineException {
    if (!object.getClass().isAnnotationPresent(Entity.class)) {
      throw Exceptions.UnableToReadAnnotation(Entity.class.getSimpleName());
    }
    Entity entityAno = object.getClass().getAnnotation(Entity.class);
    return entityAno.value();
  }

  String generateGameId(String playerName, String gameName, Difficulty difficulty, String level) {
    String fullGameName = Utils.getFullGameName(gameName, difficulty);
    return StringUtils.capitalize(playerName) + "_" + StringUtils.capitalize(fullGameName) + "_" + level;
  }

  String generateObjectId(String objectName) {
    String randomId = UUID.randomUUID().toString().replace("-", "");
    return StringUtils.capitalize(objectName) + randomId;
  }
}
