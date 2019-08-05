package com.aegean.icsd.engine.generator.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.queries.InsertQuery;
import com.aegean.icsd.queries.SelectQuery;
import com.aegean.icsd.queries.beans.InsertParam;

import com.google.gson.JsonArray;

@Repository
public class GeneratorDao implements IGeneratorDao {

  static final String HAS_PLAYER = "hasPlayer";
  static final String HAS_LEVEL = "hasLevel";
  static final String HAS_GAME_ID = "hasGameId";

  @Autowired
  private IOntology ontology;

  @Override
  public int getLatestLevel(String gameName, String playerName) throws EngineException {
    SelectQuery selectQuery = new SelectQuery.Builder()
      .select("latestLevel")
      .where("s", "type", "class")
      .where("s", HAS_PLAYER, "playerName")
      .where("s", HAS_LEVEL, "latestLevel")
      .where("s", "completed", "date")
      .orderByDesc("date")
      .limit(1)
      .addIriParam("type", "rdf:type")
      .addIriParam("class", ontology.getPrefixedEntity(gameName))
      .addIriParam(HAS_PLAYER, ontology.getPrefixedEntity(HAS_PLAYER))
      .addIriParam(HAS_LEVEL, ontology.getPrefixedEntity(HAS_LEVEL))
      .addIriParam("completed", ontology.getPrefixedEntity("completedDate"))
      .addLiteralParam("playerName", playerName)
      .build();

    JsonArray result;
    try {
      result = ontology.select(selectQuery);
    } catch (OntologyException e) {
      throw DaoExceptions.SelectQuery("Error when retrieving the latest level", e);
    }
    int latestLevel = result.get(0).getAsJsonObject().get("latestLevel").getAsInt();

    return latestLevel;
  }

  @Override
  public boolean generateBasicGame(GameInfo info) throws EngineException {
    String prefixedFullGameName = ontology.getPrefixedEntity(Utils.getFullGameName(info.getGameName(), info.getDifficulty()));

    InsertQuery ins = new InsertQuery.Builder()
      .insertEntry(ontology.getPrefixedEntity(info.getId()), prefixedFullGameName)
      .addRelation(InsertParam.createObj(ontology.getPrefixedEntity(HAS_PLAYER)),
        InsertParam.createValue(info.getPlayerName()))
      .addRelation(InsertParam.createObj(ontology.getPrefixedEntity(HAS_GAME_ID)),
        InsertParam.createValue(info.getId()))
      .build();

    try {
      return ontology.insert(ins);
    } catch (OntologyException e) {
      throw DaoExceptions.InsertQuery("Game: " + prefixedFullGameName, e);
    }
  }

  @Override
  public String getPrefixedName(String entity) {
    return ontology.getPrefixedEntity(entity);
  }

  @Override
  public boolean createValueRelation(String id, String name, String rangeValue) throws EngineException {
    return createRelation(id, name, rangeValue,false);
  }

  @Override
  public boolean createObjRelation(String id, String name, String objId) throws EngineException {
    return createRelation(id, name, objId,true);
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
  public boolean isCreated(String id) throws EngineException {
    SelectQuery selectQuery = new SelectQuery.Builder()
      .select("class")
      .where("id", "type", "class")
      .limit(1)
      .addIriParam("id", ontology.getPrefixedEntity(id))
      .addIriParam("type", ontology.getPrefixedEntity("rdf:type"))
      .build();

    JsonArray result;
    try {
      result = ontology.select(selectQuery);
    } catch (OntologyException e) {
      throw DaoExceptions.SelectQuery("Error when checking the existence of the id", e);
    }

    String entityClass = result.get(0).getAsJsonObject().get("class").getAsString();
    return entityClass != null;
  }

  boolean createRelation(String id, String name, String rangeValue, boolean isObject) throws EngineException {
    InsertParam rangeParam;
    if (isObject) {
      rangeParam = InsertParam.createObj(ontology.getPrefixedEntity(rangeValue));
    } else {
      rangeParam = InsertParam.createValue(rangeValue);
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
}
