package com.aegean.icsd.mciwebapp.synonym.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;
import com.aegean.icsd.ontology.interfaces.IOntologyConnector;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;

@Repository
public class SynonymDao implements ISynonymDao {

  @Autowired
  private IOntologyConnector ont;

  @Autowired
  private IMciModelReader model;

  @Override
  public String getMainWord(String id) throws MciException {
    SelectQuery sQ = new SelectQuery.Builder()
      .select("word")
      .where("s", "hasId", "id")
      .where("s", "hasMainWord","word")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasMainWord", model.getPrefixedEntity("hasMainWord"))
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
