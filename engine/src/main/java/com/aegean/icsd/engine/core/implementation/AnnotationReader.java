package com.aegean.icsd.engine.core.implementation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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

  private static Logger LOGGER = Logger.getLogger(AnnotationReader.class);

  @Override
  public String setEntityId(Object object) throws EngineException {
    String objName = getEntityValue(object.getClass());
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

    Object idObj = invokeFieldGetter(idField, object);
    String id = idObj != null ? idObj.toString() : null;

    if (StringUtils.isEmpty(id)) {
      Id idAno = idField.getAnnotation(Id.class);
      String masterKey;
      if (idAno.autoGenerated()) {
        masterKey = UUID.randomUUID().toString().replace("-", "");
      } else {
        masterKey = String.join("_", keys);
      }
      id = idSanitizer(objName + "_" + masterKey);
      invokeFieldSetter(idField, object, id);
      LOGGER.debug(String.format("Entity ID for object %s: %s", objName, id));
    }
    return id;
  }

  @Override
  public String getEntityValue(Class<?> objectClass) throws EngineException {
    LOGGER.debug("Reading object entity name");
    if (!objectClass.isAnnotationPresent(Entity.class)) {
      throw Exceptions.UnableToReadAnnotation(Entity.class.getSimpleName());
    }
    Entity entityAno = objectClass.getAnnotation(Entity.class);
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
          relations.put(relation, values);
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
        if (List.class.isAssignableFrom(field.getType())) {
          ParameterizedType listParamType = (ParameterizedType) field.getGenericType();
          Class<?> listType = (Class<?>) listParamType.getActualTypeArguments()[0];
          List castedList = new ArrayList();
          for (Object obj : (List) value) {
            castedList.add(parseValue(listType, obj));
          }
          invokeFieldSetter(field, object, castedList);
        } else {
          invokeFieldSetter(field, object, parseValue(fieldClass, value));
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
    List<Field> derivedClassFields = new ArrayList<>(Arrays.asList(object.getClass().getDeclaredFields()));
    List<Field> superFields = new ArrayList<>(Arrays.asList(object.getClass().getSuperclass().getDeclaredFields()));

    superFields.removeIf(superField -> {
      List<String> shadowed = derivedClassFields.stream()
        .filter(derivedField -> derivedField.getName().equals(superField.getName()))
        .map(Field::getName)
        .collect(Collectors.toList());
      return shadowed.contains(superField.getName());
    });

    derivedClassFields.addAll(superFields);
    return derivedClassFields;
  }

  Object parseValue (Class<?> clazz, Object value) {
    Object parsedValue;
    if (Integer.class.isAssignableFrom(clazz)) {
      parsedValue = Integer.parseInt(value.toString());
    } else if (Long.class.isAssignableFrom(clazz)) {
      parsedValue = Long.parseLong(value.toString());
    } else if (String.class.isAssignableFrom(clazz)) {
      parsedValue = value != null ? value.toString() : null;
    } else if (Enum.class.isAssignableFrom(clazz)) {
      parsedValue = Enum.valueOf((Class<Enum>)clazz, value.toString().toUpperCase(Locale.ENGLISH));
    } else if (Boolean.class.isAssignableFrom(clazz)) {
      parsedValue = value != null ? Boolean.valueOf(value.toString()) : null;
    } else {
      parsedValue = value;
    }
    return parsedValue;
  }

}
