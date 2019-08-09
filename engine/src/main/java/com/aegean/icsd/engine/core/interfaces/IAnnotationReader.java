package com.aegean.icsd.engine.core.interfaces;

import java.util.Map;

import com.aegean.icsd.engine.common.beans.EngineException;

public interface IAnnotationReader {

  String setEntityId(Object object) throws EngineException;

  String getEntityValue(Object object) throws EngineException;

  Map<String, Object> getDataProperties(Object object) throws EngineException;

}
