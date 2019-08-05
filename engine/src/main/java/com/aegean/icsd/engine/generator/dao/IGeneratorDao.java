package com.aegean.icsd.engine.generator.dao;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;

public interface IGeneratorDao {

  boolean generateBasicGame(GameInfo info) throws EngineException;

  boolean createStringValueRelation(String id, String name, String rangeValue) throws EngineException;

  boolean createValueRelation(String id, String name, Object rangeValue, Class<?> valueClass)
    throws EngineException;

  boolean createObjRelation(String id, String name, String objId) throws EngineException;

  boolean instantiateObject(String id, String type) throws EngineException;

  Class<?> getJavaClass(String range);
}
