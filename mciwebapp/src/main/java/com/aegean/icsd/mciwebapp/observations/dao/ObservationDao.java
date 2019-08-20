package com.aegean.icsd.mciwebapp.observations.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationItem;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;
import com.aegean.icsd.ontology.interfaces.IOntologyConnector;
import com.aegean.icsd.ontology.queries.AskQuery;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Repository
public class ObservationDao implements IObservationDao {

  private final static String gameName = "Observation";

  @Autowired
  private IOntologyConnector ont;

  @Autowired
  private IMciModelReader model;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IAnnotationReader ano;

  @Override
  public List<String> getAssociatedSubjects(String id) throws MciException {

    SelectQuery query = new SelectQuery.Builder()
      .select("word")
      .setDistinct(true)
      .where("obs", "hasId", "id")
      .where("obs", "hasObservation", "obsObj")
      .where("obsObj", "hasImage", "image")
      .where("image", "hasSubject", "subject")
      .where("subject", "hasStringValue", "word")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasObservation", model.getPrefixedEntity("hasObservation"))
      .addIriParam("hasSubject", model.getPrefixedEntity("hasSubject"))
      .addIriParam("hasStringValue", model.getPrefixedEntity("hasStringValue"))
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
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasImage", model.getPrefixedEntity("hasImage"))
      .addIriParam("hasAssetPath", model.getPrefixedEntity("hasAssetPath"))
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
  public List<ObservationItem> getObservationItems(String id) throws MciException {
    SelectQuery q = new SelectQuery.Builder()
      .select("nb", "path")
      .where("s", "hasId", "id")
      .where("s", "hasObservation", "obs")
      .where("obs", "hasImage", "img")
      .where("obs", "hasTotalImages", "nb")
      .where("img", "hasAssetPath", "path")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasObservation", model.getPrefixedEntity("hasObservation"))
      .addIriParam("hasImage", model.getPrefixedEntity("hasImage"))
      .addIriParam("hasTotalImages", model.getPrefixedEntity("hasTotalImages"))
      .addIriParam("hasAssetPath", model.getPrefixedEntity("hasAssetPath"))
      .addLiteralParam("id", id)
      .build();

    try {
      List<ObservationItem> items = new ArrayList<>();
      JsonArray results = ont.select(q);
      for (JsonElement entry : results) {
        ObservationItem item = new ObservationItem();
        item.setTotalInstances(entry.getAsJsonObject().get("nb").getAsInt());
        item.setImage(entry.getAsJsonObject().get("path").getAsString());
        items.add(item);
      }
      return items;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveObservationItems(id, e);
    }
  }

  @Override
  public boolean solveGame(String id, String player, String word, Integer occurrences) throws MciException {

    AskQuery ask = new AskQuery.Builder()
      .is("s", "hasId", "id")
      .is("s", "hasObservation", "obs")
      .is("s", "hasPlayer", "player")
      .is("obs", "hasImage", "img")
      .is("obs", "hasTotalImages", "occurrences")
      .is("img", "hasSubject", "sub")
      .is("sub", "hasStringValue", "word")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasObservation", model.getPrefixedEntity("hasObservation"))
      .addIriParam("hasPlayer", model.getPrefixedEntity("hasPlayer"))
      .addIriParam("hasImage", model.getPrefixedEntity("hasImage"))
      .addIriParam("hasSubject", model.getPrefixedEntity("hasSubject"))
      .addIriParam("hasTotalImages", model.getPrefixedEntity("hasTotalImages"))
      .addIriParam("hasStringValue", model.getPrefixedEntity("hasStringValue"))
      .addLiteralParam("occurrences", occurrences)
      .addLiteralParam("word", word)
      .addLiteralParam("id", id)
      .addLiteralParam("player", player)
      .build();

    try {
      return ont.ask(ask);
    } catch (OntologyException e) {
      throw Exceptions.FailedToAskTheSolution(id, player, word, occurrences, e);
    }
  }
}
