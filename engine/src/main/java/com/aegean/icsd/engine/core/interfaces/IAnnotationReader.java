package com.aegean.icsd.engine.core.interfaces;

import java.util.Map;

import com.aegean.icsd.engine.common.beans.EngineException;

public interface IAnnotationReader {

  String getEntityId(Object object) throws EngineException;

  String setEntityId(Object object) throws EngineException;

  String getEntityValue(Object object) throws EngineException;

  void setDataProperty(String propertyName, Object object, Object value) throws EngineException;

  Map<String, Object> getDataProperties(Object object) throws EngineException;

}
