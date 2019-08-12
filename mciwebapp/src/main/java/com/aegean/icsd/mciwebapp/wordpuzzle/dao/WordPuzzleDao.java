package com.aegean.icsd.mciwebapp.wordpuzzle.dao;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;

@Repository
public class WordPuzzleDao implements IWordPuzzleDao {

  @Autowired
  private IOntology ont;

  @Override
  public
  boolean solveGame(String id, String player, String key, Integer value) throws MciException {
    return false;
  }

  @Override
  public String getWordById(String id) throws MciException {
    SelectQuery q = new SelectQuery.Builder()
      .select("value")
      .where("g", "hasId", "id")
      .where("g", "hasWord", "word")
      .where("word", "hasStringValue", "value")
      .build();

    try {
      JsonArray result = ont.select(q);
      String value = "";
      if (result.size() > 0) {
        value = result.get(0).getAsJsonObject().get("value").getAsString();
      }
      return value;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveWord(id,e);
    }
  }
}
