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
  public String getNewWordIdFor(String forEntity) throws ProviderException {
    SelectQuery q = new SelectQuery.Builder()
      .select("wordId")
      .whereHasType("s", ont.getPrefixedEntity(forEntity))
      .whereHasType("word", ont.getPrefixedEntity(Word.NAME))
      .where("word", "hasId", "wordId")
      .minus("s", "p", "word")
      .limit(1)
      .addIriParam("hasId", ont.getPrefixedEntity("hasId"))
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
}
