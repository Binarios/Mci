package com.aegean.icsd.mciwebapp.object.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;

public class ObjectsDao implements IObjectsDao {

  @Autowired
  private IOntology ont;

  @Override
  public List<String> getWordIdsWithLength(int length) throws ProviderException {
    SelectQuery q = new SelectQuery.Builder()
      .select("wordId")
      .whereHasType("s", "Word")
      .where("s", "hasId", "wordId")
      .where("s", "hasStringValue", "word")
      .filterByStrLength("word", SelectQuery.Builder.Operator.EQ, "wordLength")
      .addIriParam("Word", ont.getPrefixedEntity("Word"))
      .addIriParam("hasId", ont.getPrefixedEntity("hasId"))
      .addIriParam("hasStringValue", ont.getPrefixedEntity("hasStringValue"))
      .addLiteralParam("wordLength", length)
      .build();

    try {
      JsonArray results = ont.select(q);
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveWords(e);
    }

    return null;
  }
}
