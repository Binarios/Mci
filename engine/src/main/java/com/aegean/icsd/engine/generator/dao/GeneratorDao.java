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
  static final String HAS_DIFFICULTY = "hasDifficulty";
  static final String HAS_GAME_ID = "hasGameId";
  static final String MAX_COMPLETION_TIME = "maxCompletionTime";

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
    String prefixedFullGameName = getPrefixedName(Utils.getFullGameName(info.getGameName(), info.getDifficulty()));

    InsertQuery ins = new InsertQuery.Builder()
      .insertEntry(getPrefixedName(info.getId()), prefixedFullGameName)
      .addRelation(InsertParam.createObj(getPrefixedName(MAX_COMPLETION_TIME)),
        InsertParam.createValue(info.getMaxCompletionTime()))
      .addRelation(InsertParam.createObj(getPrefixedName(HAS_PLAYER)),
        InsertParam.createValue(info.getPlayerName()))
      .addRelation(InsertParam.createObj(getPrefixedName(HAS_DIFFICULTY)),
        InsertParam.createValue(info.getDifficulty().getNormalizedName()))
      .addRelation(InsertParam.createObj(getPrefixedName(HAS_GAME_ID)),
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
  public String generateNodeName(String entity) {
    return ontology.nodeNameGenerator(entity.replace(":", "_"));
  }
}
