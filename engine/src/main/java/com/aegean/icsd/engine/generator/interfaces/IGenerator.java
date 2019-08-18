package com.aegean.icsd.engine.generator.interfaces;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.BaseGame;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;

public interface IGenerator {

  <T extends BaseGameObject> List<T> selectGameObject(T criteria) throws EngineException;

  <T extends BaseGame> String upsertGame(T game) throws EngineException;

  <T extends BaseGameObject> String upsertGameObject(T gameObject) throws EngineException;

  boolean createObjRelation(String id, EntityProperty onProperty, String rangeId) throws EngineException;

  int getLastCompletedLevel(String gameName, Difficulty difficulty, String playerName) throws EngineException;

  <T extends BaseGame> List<T> getGamesForPlayer(String playerName, Class<T> gameObjClass)
      throws EngineException;

  <T extends BaseGame> T getGameWithId(String id, String playerName, Class<T> gameObjClass)
    throws EngineException;

  Long generateLongDataValue(ValueRangeRestriction res);
}
