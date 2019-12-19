package com.aegean.icsd.engine.generator.dao;

import java.util.List;
import java.util.Map;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.common.beans.BaseGame;
import com.aegean.icsd.engine.common.beans.BaseGameObject;

public interface IGeneratorDao {

  <T extends BaseGameObject> List<T> selectGameObject(Map<String, Object> relations, Class<T> aClass) throws EngineException;

  <T extends BaseGame> List<T> selectGame(Map<String, Object> relations, Class<T> aClass)  throws EngineException;

  boolean createValueRelation(String id, String name, Object rangeValue, Class<?> valueClass)
    throws EngineException;

  boolean createObjRelation(String id, String name, String objId) throws EngineException;

  int getLastCompletedLevel(String gameName, Difficulty difficulty, String playerName) throws EngineException;

  boolean instantiateObject(String id, String type) throws EngineException;

}
