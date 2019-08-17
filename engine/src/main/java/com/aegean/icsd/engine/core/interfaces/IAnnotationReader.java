package com.aegean.icsd.engine.core.interfaces;

import java.util.Map;

import com.aegean.icsd.engine.common.beans.EngineException;

public interface IAnnotationReader {

  String setEntityId(Object object) throws EngineException;

  String getEntityValue(Class<?> objectClass) throws EngineException;

  Map<String, Object> getDataProperties(Object object) throws EngineException;

  void setDataPropertyValue(Object object, String property, Object value) throws EngineException;

}
