package com.aegean.icsd.mciwebapp.object.dao;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;
import com.aegean.icsd.ontology.interfaces.IOntologyConnector;
import com.aegean.icsd.ontology.queries.AskQuery;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Repository
public class ObjectsDao implements IObjectsDao {

  @Autowired
  private IOntologyConnector ont;

  @Autowired
  private IMciModelReader model;

  @Override
  public List<String> getNewWordIdsFor(String forEntity) throws ProviderException {
    SelectQuery q = new SelectQuery.Builder()
      .select("wordId")
      .whereHasType("s", model.getPrefixedEntity(forEntity))
      .whereHasType("word", model.getPrefixedEntity(Word.NAME))
      .where("word", "hasId", "wordId")
      .minus("s", "p", "word")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .build();

    try {
      JsonArray results = ont.select(q);
      List<String> ids = new ArrayList<>();
      for (JsonElement result : results) {
        ids.add(result.getAsJsonObject().get("wordId").getAsString());
      }
      return ids;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveWords(Word.NAME, e);
    }
  }

  @Override
  public List<String> getAssociatedWordOfId(String id) throws ProviderException {
    SelectQuery q = new SelectQuery.Builder()
      .select("associatedId")
      .where("s", "p", "id")
      .where("s", "pAll", "w")
      .whereHasType("w",  model.getPrefixedEntity(Word.NAME))
      .where("w", "hasId", "associatedId")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addLiteralParam("id", id)
      .build();

    try {
      JsonArray results = ont.select(q);
      List<String> ids = new ArrayList<>();
      for (JsonElement result : results) {
        ids.add(result.getAsJsonObject().get("associatedId").getAsString());
      }
      return ids;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveWords(id, e);
    }
  }

  @Override
  public boolean areSynonyms(Word thisWord, Word otherWord) throws ProviderException {
    AskQuery ask = new AskQuery.Builder()
      .is("this", "hasId", "thisId")
      .is("this", "hasSynonym", "other")
      .is("other", "hasId", "otherId")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasSynonym", model.getPrefixedEntity("hasSynonym"))
      .addLiteralParam("thisId", thisWord.getId())
      .addLiteralParam("otherId", otherWord.getId())
      .build();

    try {
      return ont.ask(ask);
    } catch (OntologyException e) {
      throw Exceptions.FailedToAsk(String.format("%s is not synonym with %s", thisWord.getValue(), otherWord.getValue() ), e);
    }
  }

  @Override
  public boolean areAntonyms(Word thisWord, Word otherWord) throws ProviderException {
    AskQuery ask = new AskQuery.Builder()
      .is("this", "hasId", "thisId")
      .is("this", "hasAntonym", "other")
      .is("other", "hasId", "otherId")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasAntonym", model.getPrefixedEntity("hasAntonym"))
      .addLiteralParam("thisId", thisWord.getId())
      .addLiteralParam("otherId", otherWord.getId())
      .build();

    try {
      return ont.ask(ask);
    } catch (OntologyException e) {
      throw Exceptions.FailedToAsk(String.format("%s is not antonym with %s", thisWord.getValue(), otherWord.getValue() ), e);
    }
  }
}
