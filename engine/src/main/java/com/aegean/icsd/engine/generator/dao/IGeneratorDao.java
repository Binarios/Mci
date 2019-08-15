package com.aegean.icsd.engine.generator.dao;

import java.util.List;
import java.util.Map;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.BaseGame;

public interface IGeneratorDao {

  Map<String, Object> selectObject(Map<String, Object> relations) throws EngineException;

  boolean createValueRelation(String id, String name, Object rangeValue, Class<?> valueClass)
    throws EngineException;

  boolean createObjRelation(String id, String name, String objId) throws EngineException;

  boolean instantiateObject(String id, String type) throws EngineException;

  int getLastCompletedLevel(String gameName, Difficulty difficulty, String playerName) throws EngineException;

  <T extends BaseGame> List<T> getGamesForPlayer(String gameName, String playerName, Class<T> gameObjClass)
    throws EngineException;

  <T extends BaseGame> T getGameWithId(String id, String playerName, Class<T> gameObjClass)
    throws EngineException;
}
