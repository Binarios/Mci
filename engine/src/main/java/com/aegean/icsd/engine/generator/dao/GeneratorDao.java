package com.aegean.icsd.engine.generator.dao;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.beans.BaseGame;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;
import com.aegean.icsd.ontology.interfaces.IOntologyConnector;
import com.aegean.icsd.ontology.queries.InsertQuery;
import com.aegean.icsd.ontology.queries.SelectQuery;
import com.aegean.icsd.ontology.queries.beans.InsertParam;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Repository
public class GeneratorDao implements IGeneratorDao {

  @Autowired
  private IOntologyConnector ontology;

  @Autowired
  private IMciModelReader model;

  @Autowired
  private IAnnotationReader ano;

  @Override
  public <T extends BaseGameObject> List<T> selectGameObject(Map<String, Object> relations, Class<T> aClass) throws EngineException {
    SelectQuery.Builder qBuilder = new SelectQuery.Builder()
      .select("s", "p", "o");

    int i = 0;
    for (Map.Entry<String, Object> entry : relations.entrySet()) {
      String dataProperty = entry.getKey();
      Object value = entry.getValue();
      if (value == null) {
        continue;
      }
      String param = "value" + i;
      qBuilder.where("s", dataProperty, param)
        .addIriParam(dataProperty, model.getPrefixedEntity(dataProperty));
      if (Integer.class.isAssignableFrom(value.getClass())) {
        qBuilder.addLiteralParam(param, Integer.parseInt(value.toString()));
      } else if (Long.class.isAssignableFrom(value.getClass())) {
        qBuilder.addLiteralParam(param, Long.parseLong(value.toString()));
      } else if (Boolean.class.isAssignableFrom(value.getClass())) {
        qBuilder.addLiteralParam(param, Boolean.parseBoolean(value.toString()));
      } else {
        qBuilder.addLiteralParam(param, value.toString());
      }
      i++;
    }
    qBuilder.where("s", "p", "o")
      .filter("o", SelectQuery.Builder.Operator.IS_LITERAL, "");

    try {
      JsonArray results = ontology.select(qBuilder.build());
      List<T> objects = new ArrayList<>();
      Map<String, JsonArray> groupedByNodeName = groupByNodeName("s", results);
      for (Map.Entry<String, JsonArray> entry : groupedByNodeName.entrySet()) {
        try {
          T object = mapJsonToObject(entry.getValue(), aClass);
          objects.add(object);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
          throw DaoExceptions.ConstructorNotFound(aClass.getSimpleName(), e);
        }
      }
      return objects;
    } catch (OntologyException e) {
      throw DaoExceptions.SelectObjectQuery("No extra msg", e);
    }
  }

  @Override
  public boolean createValueRelation(String id, String name, Object rangeValue, Class<?> valueClass)
    throws EngineException {
    if (rangeValue != null) {
      return createRelation(id, name, rangeValue, false, valueClass);
    }
    return true;
  }

  @Override
  public boolean createObjRelation(String id, String name, String objId) throws EngineException {
    return createRelation(id, name, objId, true, null);
  }

  @Override
  public boolean instantiateObject(String id, String type) throws EngineException {
    InsertQuery ins = new InsertQuery.Builder()
      .insertEntry(model.getPrefixedEntity(id), model.getPrefixedEntity(type))
      .build();

    try {
      return ontology.insert(ins);
    } catch (OntologyException e) {
      throw DaoExceptions.InsertQuery("Object: " + id, e);
    }
  }

  @Override
  public int getLastCompletedLevel(String gameName, Difficulty difficulty, String playerName) throws EngineException {
    SelectQuery query = new SelectQuery.Builder()
      .select("level")
      .whereHasType("obs", model.getPrefixedEntity(gameName))
      .where("obs", "hasDifficulty", "difficulty")
      .where("obs", "hasPlayer", "playerName")
      .where("obs", "hasLevel", "level")
      .orderByDesc("level")
      .limit(1)
      .addIriParam("hasDifficulty", model.getPrefixedEntity("hasDifficulty"))
      .addIriParam("hasPlayer", model.getPrefixedEntity("hasPlayer"))
      .addIriParam("hasLevel", model.getPrefixedEntity("hasLevel"))
      .addLiteralParam("difficulty", difficulty.name())
      .addLiteralParam("playerName", playerName)
      .build();

    try {
      int level = 0;
      JsonArray results = ontology.select(query);
      if (results.size() > 0) {
        level = results.get(0).getAsJsonObject().get("level").getAsInt();
      }
      return level;
    } catch (OntologyException e) {
      throw DaoExceptions.FailedToRetrieveLastLevel(gameName, difficulty, playerName, e);
    }
  }

  @Override
  public <T extends BaseGame> List<T> getGamesForPlayer(String gameName, String playerName, Class<T> gameObjClass)
    throws EngineException {
    SelectQuery query = new SelectQuery.Builder()
      .select("s", "p", "o")
      .where("s", "rdfType", "type")
      .where("s", "hasPlayer", "player")
      .where("s", "p", "o")
      .regexFilter("type", "gameName")
      .filter("o", SelectQuery.Builder.Operator.IS_LITERAL, "")
      .addIriParam("hasPlayer", model.getPrefixedEntity("hasPlayer"))
      .addIriParam("rdfType", "rdf:type")
      .addLiteralParam("player", playerName)
      .addLiteralParam("gameName", gameName)
      .build();

    try {
      JsonArray results = ontology.select(query);
      List<T> games = new ArrayList<>();
      Map<String, JsonArray> groupedByNodeName = groupByNodeName("s", results);
      for (Map.Entry<String, JsonArray> entry : groupedByNodeName.entrySet()) {
        try {
          T game = mapJsonToObject(entry.getValue(), gameObjClass);
          games.add(game);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
          throw DaoExceptions.ConstructorNotFound(gameName, e);
        }
      }

      return games;
    } catch (OntologyException e) {
      throw DaoExceptions.FailedToRetrieveGames(playerName, e);
    }
  }

  @Override
  public <T extends BaseGame> T getGameWithId(String id, String playerName, Class<T> gameObjClass) throws EngineException {
    SelectQuery query = new SelectQuery.Builder()
      .select("s", "p", "o")
      .where("s", "hasPlayer", "player")
      .where("s", "hasId", "id")
      .where("s", "p", "o")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasPlayer", model.getPrefixedEntity("hasPlayer"))
      .addLiteralParam("player", playerName)
      .addLiteralParam("id", id)
      .filter("o", SelectQuery.Builder.Operator.IS_LITERAL, "")
      .build();

    try {
      JsonArray results = ontology.select(query);
      T game = null;
      if (results.size() > 0) {
        Map<String, JsonArray> groupedByNodeName = groupByNodeName("s", results);
        for (Map.Entry<String, JsonArray> entry : groupedByNodeName.entrySet()) {
          try {
            game = mapJsonToObject(entry.getValue(), gameObjClass);
          } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw DaoExceptions.ConstructorNotFound(id, e);
          }
        }
      }
      if (game == null) {
        throw DaoExceptions.FailedToRetrieveGames(playerName);
      }
      return game;
    } catch (OntologyException e) {
      throw DaoExceptions.FailedToRetrieveGames(playerName, e);
    }
  }

  <T> boolean createRelation(String id, String name, Object rangeValue, boolean isObject, Class<T> rangeClass)
    throws EngineException {
    InsertParam rangeParam;
    if (isObject) {
      rangeParam = InsertParam.createObj(model.getPrefixedEntity(rangeValue.toString()));
    } else {
      rangeParam = InsertParam.createValue(rangeValue, rangeClass);
    }

    InsertQuery ins = new InsertQuery.Builder()
      .forSubject(InsertParam.createObj(model.getPrefixedEntity(id)))
      .addRelation(InsertParam.createObj(model.getPrefixedEntity(name)), rangeParam)
      .build();
    try {
      return ontology.insert(ins);
    } catch (OntologyException e) {
      throw DaoExceptions.InsertQuery("Game: " + id, e);
    }
  }

  <T> T mapJsonToObject(JsonArray dataProperties, Class<T> objectClass)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, EngineException {
    T object = objectClass.getDeclaredConstructor().newInstance();
    for (JsonElement element : dataProperties) {
      JsonObject obj = element.getAsJsonObject();
      String prefixedDataProperty = obj.get("p").getAsString();
      String dataProperty = model.removePrefix(prefixedDataProperty);
      String value = obj.get("o").getAsString();
      ano.setDataPropertyValue(object, dataProperty, value);
    }
    return object;
  }

  Map<String, JsonArray> groupByNodeName(String nodeParam, JsonArray results) {
    Map<String, JsonArray> groupedByNodeName = new HashMap<>();
    for (JsonElement element : results) {
      JsonObject obj = element.getAsJsonObject();
      String nodeName = obj.get(nodeParam).getAsString();
      if (groupedByNodeName.containsKey(nodeName)) {
        JsonArray existing = groupedByNodeName.get(nodeName);
        existing.add(element);
      } else {
        JsonArray obsArray = new JsonArray();
        obsArray.add(element);
        groupedByNodeName.put(nodeName, obsArray);
      }
    }
    return groupedByNodeName;
  }
}
