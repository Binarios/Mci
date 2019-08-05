package com.aegean.icsd.engine.core.implementation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Id;
import com.aegean.icsd.engine.core.annotations.Key;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;

import com.github.jsonldjava.utils.Obj;

@Service
public class AnnotationReader implements IAnnotationReader {

  @Override
  public String setEntityId(Object object) throws EngineException {
    String objName = getEntityValue(object);
    List<String> keys = new LinkedList<>();
    Field idField = null;
    for (Field field : object.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(Key.class)) {
        keys.add(invokeFieldGetter(field, object).toString());
      }
      if (field.isAnnotationPresent(Id.class)) {
        idField = field;
      }
    }

    if (idField == null) {
      throw Exceptions.UnableToReadAnnotation(Id.class.getSimpleName());
    }

    String masterKey = String.join("_", keys);
    String id = idSanitizer(objName + "_" + masterKey);
    invokeFieldSetter(idField, object, id);
    return id;
  }

  @Override
  public String getEntityValue(Object object) throws EngineException {
    if (!object.getClass().isAnnotationPresent(Entity.class)) {
      throw Exceptions.UnableToReadAnnotation(Entity.class.getSimpleName());
    }
    Entity entityAno = object.getClass().getAnnotation(Entity.class);
    return entityAno.value();
  }

  @Override
  public void setDataProperty(String propertyName, Object object, Object value) throws EngineException {
    for (Field field : object.getClass().getDeclaredFields()) {
      if (!field.isAnnotationPresent(DataProperty.class)) {
        continue;
      }
      DataProperty dataProperty = field.getAnnotation(DataProperty.class);
      List<String> values = Arrays.asList(dataProperty.value());
      if (!values.contains(propertyName)) {
        continue;
      }
      invokeFieldSetter(field,object, value);
    }
  }

  @Override
  public Map<String, Object> getDataProperties(Object object) throws EngineException {
    Map<String, Object> relations = new HashMap<>();
    for (Field field : object.getClass().getDeclaredFields()) {
      if (!field.isAnnotationPresent(DataProperty.class)) {
        continue;
      }
      DataProperty dataProperty = field.getAnnotation(DataProperty.class);
      for (String relation : dataProperty.value()) {
        if (List.class.isAssignableFrom(field.getType())) {
          List values = (List) invokeFieldGetter(field, object);
          for (Object value : values) {
            if (value != null) {
              relations.put(relation, value);
            }
          }
        } else {
          relations.put(relation, invokeFieldGetter(field, object));
        }
      }
    }
    return relations;
  }

  Object invokeFieldGetter (Field field, Object object) throws EngineException {
    try {
      Method getter;
      if (Boolean.class.equals(field.getType())
        || boolean.class.equals(field.getType())) {
        getter = object.getClass().getMethod("is" + StringUtils.capitalize(field.getName()));
      } else {
        getter = object.getClass().getMethod("get" + StringUtils.capitalize(field.getName()));
      }
      return getter.invoke(object);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw Exceptions.GenericError(e);
    }
  }

  void invokeFieldSetter (Field field, Object object, Object...args) throws EngineException {
    try {
      Method setter = object.getClass().getMethod("set" + StringUtils.capitalize(field.getName()), field.getType());
      setter.invoke(object, args);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw Exceptions.GenericError(e);
    }
  }

  String idSanitizer(String id) {
    return id.replaceAll("[^a-zA-Z\\d]", "_");
  }
}
