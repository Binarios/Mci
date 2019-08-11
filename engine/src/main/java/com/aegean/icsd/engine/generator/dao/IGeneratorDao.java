package com.aegean.icsd.engine.generator.dao;

import java.util.Map;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;

public interface IGeneratorDao {

  boolean createValueRelation(String id, String name, Object rangeValue, Class<?> valueClass)
    throws EngineException;

  boolean createObjRelation(String id, String name, String objId) throws EngineException;

  boolean instantiateObject(String id, String type) throws EngineException;

  Class<?> getJavaClass(String range);

  String selectObjectId(Map<String, Object> propValues) throws EngineException;
}
