package com.aegean.icsd.engine.generator.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.generator.dao.IGeneratorDao;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
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

  String generateGameId(String playerName, String gameName, Difficulty difficulty, String level) {
    String fullGameName = Utils.getFullGameName(gameName, difficulty);
    return StringUtils.capitalize(playerName) + "_" + StringUtils.capitalize(fullGameName) + "_" + level;
  }

  String generateObjectId(String objectName) {
    String randomId = UUID.randomUUID().toString().replace("-", "");
    return StringUtils.capitalize(objectName) + randomId;
  }
}
