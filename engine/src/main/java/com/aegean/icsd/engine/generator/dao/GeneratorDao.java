package com.aegean.icsd.engine.generator.dao;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.queries.InsertQuery;
import com.aegean.icsd.queries.SelectQuery;
import com.aegean.icsd.queries.beans.InsertParam;

import com.google.gson.JsonArray;

@Repository
public class GeneratorDao implements IGeneratorDao {

  @Autowired
  private IOntology ontology;

  @Override
  public int getLatestLevel(String gameName, String playerName) throws EngineException {
    SelectQuery selectQuery = new SelectQuery.Builder()
      .select("latestLevel")
      .where("s", "type", "class")
      .where("s", "hasPlayer", "playerName")
      .where("s", "hasLevel", "latestLevel")
      .where("s", "completed", "date")
      .orderByDesc("date")
      .limit(1)
      .addIriParam("type", "rdf:type")
      .addIriParam("class", ontology.getPrefixedEntity(gameName))
      .addIriParam("hasPlayer", ontology.getPrefixedEntity("hasPlayer"))
      .addIriParam("hasLevel", ontology.getPrefixedEntity("hasLevel"))
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
  public String generateBasicGame(String gameName) throws EngineException {
    String nodeName = generateNodeName(gameName);

    InsertParam param = new InsertParam();
    param.setName("sub");
    param.setValue(nodeName);
    param.setIriParam(true);

    InsertQuery ins = new InsertQuery.Builder()
      .insertEntry(param, ontology.getPrefixedEntity(gameName))
      .build();

    try {
      ontology.insert(ins);
    } catch (OntologyException e) {
      throw DaoExceptions.InsertQuery("Game: " + gameName, e);
    }

    return nodeName;
  }


  @Override
  public String getPrefixedName(String entity) {
    return ontology.getPrefixedEntity(entity);
  }

  @Override
  public InsertParam constructInsParam(String varName, String varValue, boolean isIri) {
    InsertParam param = new InsertParam();
    param.setName(varName);
    String value = "";
    if (isIri) {
      value = getPrefixedName(varValue);
    }
    param.setValue(value);
    param.setIriParam(isIri);
    return param;
  }

  @Override
  public String generateNodeName(String entity) {
    return ontology.nodeNameGenerator(entity.replace(":", "_"));
  }
}
