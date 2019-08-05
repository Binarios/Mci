package com.aegean.icsd.engine.generator.dao;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;

public interface IGeneratorDao {

  int getLatestLevel(String gameName, String playerName) throws EngineException;

  boolean generateBasicGame(GameInfo info) throws EngineException;

  String getPrefixedName(String entity);

  boolean createValueRelation(String id, String name, String rangeValue) throws EngineException;

  boolean createObjRelation(String id, String name, String objId) throws EngineException;

  boolean instantiateObject(String id, String type) throws EngineException;

  boolean isCreated(String id) throws EngineException;
}
