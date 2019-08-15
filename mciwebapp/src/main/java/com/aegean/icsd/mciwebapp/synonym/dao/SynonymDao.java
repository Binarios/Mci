package com.aegean.icsd.mciwebapp.synonym.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;

@Repository
public class SynonymDao implements ISynonymDao {

  @Autowired
  private IOntology ont;

  @Override
  public String getMainWord(String id) throws MciException {
    SelectQuery sQ = new SelectQuery.Builder()
      .select("word")
      .where("s", "hasId", "id")
      .where("s", "hasMainWord","word")
      .addIriParam("hasId", ont.getPrefixedEntity("hasId"))
      .addIriParam("hasMainWord", ont.getPrefixedEntity("hasMainWord"))
      .addLiteralParam("id", id)
      .build();

    try {
      JsonArray result = ont.select(sQ);
      return result.get(0).getAsJsonObject().get("word").getAsString();
    } catch (OntologyException e) {
      throw Exceptions.UnableToFindMainWord(id, e);
    }
  }
}
