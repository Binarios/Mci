package com.aegean.icsd.engine.generator.dao;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.beans.BaseGame;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.queries.InsertQuery;
import com.aegean.icsd.ontology.queries.SelectQuery;
import com.aegean.icsd.ontology.queries.beans.InsertParam;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Repository
public class GeneratorDao implements IGeneratorDao {

  @Autowired
  private IOntology ontology;

  @Autowired
  private IAnnotationReader ano;

  @Override
  public boolean createValueRelation(String id, String name, Object rangeValue, Class<?> valueClass)
    throws EngineException {
    return createRelation(id, name, rangeValue, false, valueClass);
  }

  @Override
  public boolean createObjRelation(String id, String name, String objId) throws EngineException {
    return createRelation(id, name, objId, true, null);
  }

  @Override
  public boolean instantiateObject(String id, String type) throws EngineException {
    InsertQuery ins = new InsertQuery.Builder()
      .insertEntry(ontology.getPrefixedEntity(id), ontology.getPrefixedEntity(type))
      .build();

    try {
      return ontology.insert(ins);
    } catch (OntologyException e) {
      throw DaoExceptions.InsertQuery("Object: " + id, e);
    }
  }

  @Override
  public String selectObjectId(Map<String, Object> propValues) throws EngineException {
    SelectQuery.Builder builder = new SelectQuery.Builder().select("id");

    for (Map.Entry<String, Object> propValue : propValues.entrySet()) {
      String property = propValue.getKey();
      Object value = propValue.getValue();
      if (value == null) {
        continue;
      }

      if (List.class.isAssignableFrom(value.getClass())) {
        List valueList = (List) value;
        for (Object val : valueList) {
          String propVar = UUID.randomUUID().toString().replace("-", "");
          String valueVar = UUID.randomUUID().toString().replace("-", "");
          builder.where("sub", propVar, valueVar);
          builder.addIriParam(propVar, ontology.getPrefixedEntity(property));
          builder.addLiteralParam(valueVar, val.toString());
        }
      } else {
        String propVar = UUID.randomUUID().toString().replace("-", "");
        String valueVar = UUID.randomUUID().toString().replace("-", "");
        builder.where("sub", propVar, valueVar);
        builder.addIriParam(propVar, ontology.getPrefixedEntity(property));
        builder.addLiteralParam(valueVar, value.toString());
      }
      builder.where("sub", "hasId", "id");
      builder.addIriParam("hasId", ontology.getPrefixedEntity("hasId"));
    }

    SelectQuery q = builder.build();

    try {
      String id = null;
      JsonArray result = ontology.select(q);
      if (result.size() != 0) {
        id = result.get(0).getAsJsonObject().get("id").getAsString();
      }
      return id;
    } catch (OntologyException e) {
      throw DaoExceptions.InsertQuery("No extra msg", e);
    }
  }

  @Override
  public int getLastCompletedLevel(String gameName, Difficulty difficulty, String playerName) throws EngineException {
    SelectQuery query = new SelectQuery.Builder()
      .select("level")
      .whereHasType("obs", ontology.getPrefixedEntity(gameName))
      .where("obs", "hasDifficulty", "difficulty")
      .where("obs", "hasPlayer", "playerName")
      .where("obs", "hasLevel", "level")
      .orderByDesc("level")
      .limit(1)
      .addIriParam("hasDifficulty", ontology.getPrefixedEntity("hasDifficulty"))
      .addIriParam("hasPlayer", ontology.getPrefixedEntity("hasPlayer"))
      .addIriParam("hasLevel", ontology.getPrefixedEntity("hasLevel"))
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
      .whereHasType("s", ontology.getPrefixedEntity(gameName))
      .where("s", "hasPlayer", "player")
      .where("s", "p", "o")
      .addIriParam("hasPlayer", ontology.getPrefixedEntity("hasPlayer"))
      .addLiteralParam("player", playerName)
      .filter("o", SelectQuery.Builder.Operator.IS_LITERAL, "")
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
      .addIriParam("hasId", ontology.getPrefixedEntity("hasId"))
      .addIriParam("hasPlayer", ontology.getPrefixedEntity("hasPlayer"))
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

  @Override
  public Class<?> getJavaClass(String range) {
    return ontology.getJavaClassFromOwlType(range);
  }

  <T> boolean createRelation(String id, String name, Object rangeValue, boolean isObject, Class<T> rangeClass)
    throws EngineException {
    InsertParam rangeParam;
    if (isObject) {
      rangeParam = InsertParam.createObj(ontology.getPrefixedEntity(rangeValue.toString()));
    } else {
      rangeParam = InsertParam.createValue(rangeValue, rangeClass);
    }

    InsertQuery ins = new InsertQuery.Builder()
      .forSubject(InsertParam.createObj(ontology.getPrefixedEntity(id)))
      .addRelation(InsertParam.createObj(ontology.getPrefixedEntity(name)), rangeParam)
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
      String dataProperty = ontology.removePrefix(prefixedDataProperty);
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
