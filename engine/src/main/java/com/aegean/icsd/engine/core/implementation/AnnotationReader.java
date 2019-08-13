package com.aegean.icsd.engine.core.implementation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Id;
import com.aegean.icsd.engine.core.annotations.Key;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;

@Service
public class AnnotationReader implements IAnnotationReader {

  private static Logger LOGGER = Logger.getLogger(AnnotationReader.class);

  @Override
  public String setEntityId(Object object) throws EngineException {
    String objName = getEntityValue(object);
    LOGGER.debug(String.format("Setting Entity ID for object %s", objName));
    List<String> keys = new LinkedList<>();
    Field idField = null;
    List<Field> totalFields = getTotalFields(object);
    for (Field field : totalFields) {
      if (field.isAnnotationPresent(Id.class)) {
        idField = field;
      }
      if (field.isAnnotationPresent(Key.class)) {
        keys.add(invokeFieldGetter(field, object).toString());
      }
    }

    if (idField == null) {
      throw Exceptions.UnableToReadAnnotation(Id.class.getSimpleName());
    }

    Id idAno = idField.getAnnotation(Id.class);
    String masterKey;
    if (idAno.autoGenerated()) {
      masterKey = UUID.randomUUID().toString().replace("-", "");
    } else {
      masterKey = String.join("_", keys);
    }

    String id = idSanitizer(objName + "_" + masterKey);
    invokeFieldSetter(idField, object, id);
    LOGGER.debug(String.format("Entity ID for object %s: %s", objName, id));
    return id;
  }

  @Override
  public String getEntityValue(Object object) throws EngineException {
    LOGGER.debug("Reading object entity name");
    if (!object.getClass().isAnnotationPresent(Entity.class)) {
      throw Exceptions.UnableToReadAnnotation(Entity.class.getSimpleName());
    }
    Entity entityAno = object.getClass().getAnnotation(Entity.class);
    return entityAno.value();
  }

  @Override
  public Map<String, Object> getDataProperties(Object object) throws EngineException {
    LOGGER.debug("Reading data properties");
    Map<String, Object> relations = new HashMap<>();
    List<Field> totalFields = getTotalFields(object);
    for (Field field : totalFields) {
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

  @Override
  public void setDataPropertyValue(Object object, String property, Object value) throws EngineException {
    List<Field> totalFields = getTotalFields(object);
    for (Field field : totalFields) {
      if (!field.isAnnotationPresent(DataProperty.class)) {
        continue;
      }
      DataProperty dataProperty = field.getAnnotation(DataProperty.class);
      boolean found = false;
      for (String prop : dataProperty.value()) {
        found = prop.equals(property);
        if (found) {
          break;
        }
      }
      if (found) {
        Class<?> fieldClass = field.getType();
        if(Integer.class.isAssignableFrom(fieldClass)) {
          invokeFieldSetter(field, object, Integer.parseInt(value.toString()));
        }
        if(Long.class.isAssignableFrom(fieldClass)) {
          invokeFieldSetter(field, object, Long.parseLong(value.toString()));
        }
        if(String.class.isAssignableFrom(fieldClass)) {
          invokeFieldSetter(field, object, value.toString());
        }
        if(Enum.class.isAssignableFrom(fieldClass)) {
          invokeFieldSetter(field, object, Enum.valueOf((Class<Enum>)fieldClass, value.toString().toUpperCase()));
        }

        break;
      }
    }
  }

  Object invokeFieldGetter (Field field, Object object) throws EngineException {
    try {
      Method getter;
      if (Boolean.class.equals(field.getType())) {
        getter = object.getClass().getMethod("is" + StringUtils.capitalize(field.getName()));
        if (getter == null) {
          getter = object.getClass().getSuperclass().getMethod("is" + StringUtils.capitalize(field.getName()));
        }
      } else {
        getter = object.getClass().getMethod("get" + StringUtils.capitalize(field.getName()));
        if (getter == null) {
          getter = object.getClass().getSuperclass().getMethod("get" + StringUtils.capitalize(field.getName()));
        }
      }

      return getter.invoke(object);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw Exceptions.GenericError(e);
    }
  }

  void invokeFieldSetter (Field field, Object object, Object...args) throws EngineException {
    try {
      Method setter = object.getClass().getMethod("set" + StringUtils.capitalize(field.getName()), field.getType());
      if (setter == null) {
        setter = object.getClass().getSuperclass().getMethod("set" + StringUtils.capitalize(field.getName()));
      }
      setter.invoke(object, args);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw Exceptions.GenericError(e);
    }
  }

  String idSanitizer(String id) {
    return id.replaceAll("[^a-zA-Z\\d]", "_");
  }

  List<Field> getTotalFields (Object object) {
    //Arrays.asList returns an unmodifiable List. That is why we create a new one
    List<Field> totalFields = new ArrayList<>(Arrays.asList(object.getClass().getSuperclass().getDeclaredFields()));
    List<Field> derivedClassFields = new ArrayList<>(Arrays.asList(object.getClass().getDeclaredFields()));
    totalFields.addAll(derivedClassFields);
    return totalFields;
  }
}
