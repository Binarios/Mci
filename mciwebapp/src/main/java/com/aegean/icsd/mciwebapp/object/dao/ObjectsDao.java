package com.aegean.icsd.mciwebapp.object.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;

@Repository
public class ObjectsDao implements IObjectsDao {

  @Autowired
  private IOntology ont;

  @Override
  public String getNonAssociatedWordIdsWithLength(String forEntity, Integer length) throws ProviderException {
    SelectQuery q = new SelectQuery.Builder()
      .select("wordId")
      .whereHasType("s", ont.getPrefixedEntity(forEntity))
      .where("word", "hasId", "wordId")
      .where("word", "hasStringValue", "value")
      .minus("s", "hasWord", "word")
      .filterByStrLength("word", SelectQuery.Builder.Operator.EQ, "wordLength")
      .limit(1)
      .addIriParam("hasWord", ont.getPrefixedEntity("hasWord"))
      .addIriParam("hasId", ont.getPrefixedEntity("hasId"))
      .addIriParam("hasStringValue", ont.getPrefixedEntity("hasStringValue"))
      .addLiteralParam("wordLength", length)
      .build();

    try {
      JsonArray results = ont.select(q);
      String wordId = null;
      if (results.size() > 0) {
        wordId = results.get(0).getAsJsonObject().get("wordId").getAsString();
      }
      return wordId;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveWords(Word.NAME, e);
    }
  }

  @Override
  public String getWordValue(String wordId) throws ProviderException {
    SelectQuery q = new SelectQuery.Builder()
        .select("value")
        .where("word", "hasId", "wordId")
        .where("word", "hasStringValue", "value")
        .addIriParam("hasId", ont.getPrefixedEntity("hasId"))
        .addIriParam("hasStringValue", ont.getPrefixedEntity("hasStringValue"))
        .addLiteralParam("wordId", wordId)
        .build();

    try {
      JsonArray results = ont.select(q);
      String value = null;
      if (results.size() > 0) {
        value = results.get(0).getAsJsonObject().get("value").getAsString();
      }
      return value;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveWords(Word.NAME, e);
    }
  }
}
