package com.aegean.icsd.mciwebapp.observations.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;
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

  @Override
  public String getLastCompletedLevel(Difficulty difficulty, String playerName) {
    GameInfo individualInfo = generator.getLastGeneratedIndividual(gameName, difficulty, playerName);
    String lastLevel = "0";
    if (individualInfo != null) {
      lastLevel = individualInfo.getLevel();
    }
    return lastLevel;
  }

  @Override
  public String getAssociatedSubject(String observationObjId) throws ObservationsException {

    SelectQuery query = new SelectQuery.Builder()
      .select("word")
      .setDistinct(true)
      .where("obsObj", "hasId", "id")
      .where("obsObj", "hasImage", "image")
      .where("image", "hasImageSubject", "subject")
      .where("subject", "hasStringValue", "word")
      .addIriParam("hasId", ont.getPrefixedEntity("hasId"))
      .addIriParam("hasImageSubject", ont.getPrefixedEntity("hasImageSubject"))
      .addIriParam("hasStringValue", ont.getPrefixedEntity("hasStringValue"))
      .addLiteralParam("id", observationObjId)
      .build();

    try {
      JsonArray results = ont.select(query);
      String word = results.get(0).getAsJsonObject().get("word").getAsString();
      return word;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveWords(observationObjId ,e);
    }
  }

}
