package com.aegean.icsd.mciwebapp.observations.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Repository
public class ObservationDao implements IObservationDao {

  private final static String gameName = "Observation";

  @Autowired
  private IOntology ont;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IAnnotationReader ano;

  @Override
  public int getLastCompletedLevel(Difficulty difficulty, String playerName) throws MciException {
    SelectQuery query = new SelectQuery.Builder()
      .select("level")
      .whereHasType("obs", ont.getPrefixedEntity(gameName))
      .where("obs", "hasDifficulty", "difficulty")
      .where("obs", "hasPlayer", "playerName")
      .where("obs", "hasLevel", "level")
      .orderByDesc("level")
      .limit(1)
      .addIriParam("hasDifficulty", ont.getPrefixedEntity("hasDifficulty"))
      .addIriParam("hasPlayer", ont.getPrefixedEntity("hasPlayer"))
      .addIriParam("hasLevel", ont.getPrefixedEntity("hasLevel"))
      .addLiteralParam("difficulty", difficulty.name())
      .addLiteralParam("playerName", playerName)
      .build();

    try {
      int level = 0;
      JsonArray results = ont.select(query);
      if (results.size() > 0) {
        level = results.get(0).getAsJsonObject().get("level").getAsInt();
      }
      return level;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveLastLevel(gameName, difficulty, playerName, e);
    }
  }

  @Override
  public List<String> getAssociatedSubjects(String id) throws MciException {

    SelectQuery query = new SelectQuery.Builder()
      .select("word")
      .setDistinct(true)
      .where("obs", "hasId", "id")
      .where("obs", "hasObservation", "obsObj")
      .where("obsObj", "hasImage", "image")
      .where("image", "hasImageSubject", "subject")
      .where("subject", "hasStringValue", "word")
      .addIriParam("hasId", ont.getPrefixedEntity("hasId"))
      .addIriParam("hasObservation", ont.getPrefixedEntity("hasObservation"))
      .addIriParam("hasImageSubject", ont.getPrefixedEntity("hasImageSubject"))
      .addIriParam("hasStringValue", ont.getPrefixedEntity("hasStringValue"))
      .addLiteralParam("id", id)
      .build();

    try {
      JsonArray results = ont.select(query);
      List<String> words = new ArrayList<>();
      for (JsonElement elem : results) {
        if (!elem.isJsonNull()) {
          words.add(elem.getAsJsonObject().get("word").getAsString());
        }
      }
      return words;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveWords(id ,e);
    }
  }

  @Override
  public String getImagePath(String id) throws MciException {
    SelectQuery query = new SelectQuery.Builder()
      .select("path")
      .where("obsObj", "hasId", "id")
      .where("obsObj", "hasImage", "image")
      .where("image", "hasAssetPath", "path")
      .addIriParam("hasId", ont.getPrefixedEntity("hasId"))
      .addIriParam("hasImage", ont.getPrefixedEntity("hasImage"))
      .addIriParam("hasAssetPath", ont.getPrefixedEntity("hasAssetPath"))
      .addLiteralParam("id", id)
      .build();

    try {
      JsonArray results = ont.select(query);
      return  results.get(0).getAsJsonObject().get("path").getAsString();
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrievePaths(id ,e);
    }
  }

  @Override
  public List<Observation> getGamesForPlayer(String playerName) throws MciException {
    SelectQuery query = new SelectQuery.Builder()
      .select("obs", "p", "o")
      .whereHasType("obs", ont.getPrefixedEntity(gameName))
      .where("obs", "hasPlayer", "player")
      .where("obs", "p", "o")
      .addIriParam("hasId", ont.getPrefixedEntity("hasId"))
      .addIriParam("hasImage", ont.getPrefixedEntity("hasImage"))
      .addIriParam("hasAssetPath", ont.getPrefixedEntity("hasAssetPath"))
      .addLiteralParam("player", playerName)
      .filter("o", SelectQuery.Builder.Operator.IS_LITERAL, "")
      .build();

    try {
      JsonArray results = ont.select(query);
      List<Observation> observations = new ArrayList<>();
      Map<String, JsonArray> groupedByNodeName = new HashMap<>();
      for (JsonElement element : results) {
        JsonObject obj = element.getAsJsonObject();
        String nodeName = obj.get("obs").getAsString();
        if (groupedByNodeName.containsKey(nodeName)) {
          JsonArray existing = groupedByNodeName.get(nodeName);
          existing.add(element);
        } else {
          JsonArray obsArray = new JsonArray();
          obsArray.add(element);
          groupedByNodeName.put(nodeName, obsArray);
        }
      }

      for (Map.Entry<String, JsonArray> entry : groupedByNodeName.entrySet()) {
        Observation obs = new Observation();
        for (JsonElement element : entry.getValue()) {
          JsonObject obj = element.getAsJsonObject();
          String prefixedDataProperty = obj.get("p").getAsString();
          String dataProperty = ont.removePrefix(prefixedDataProperty);
          String value = obj.get("o").getAsString();
          ano.setDataPropertyValue(obs,  dataProperty, value);
        }
        observations.add(obs);
      }

      return  observations;
    } catch (OntologyException | EngineException e) {
      throw Exceptions.FailedToRetrieveGames(playerName, e);
    }
  }

}
