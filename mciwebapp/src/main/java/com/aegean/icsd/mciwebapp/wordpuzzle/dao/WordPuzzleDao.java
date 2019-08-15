package com.aegean.icsd.mciwebapp.wordpuzzle.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;
import com.aegean.icsd.ontology.interfaces.IOntologyConnector;
import com.aegean.icsd.ontology.queries.AskQuery;

@Repository
public class WordPuzzleDao implements IWordPuzzleDao {

  @Autowired
  private IOntologyConnector ont;

  @Autowired
  private IMciModelReader model;

  @Override
  public
  boolean solveGame(String id, String player, String word) throws MciException {
    AskQuery q = new AskQuery.Builder()
      .is("wp", "hasId", "id")
      .is("wp", "hasPlayer", "player")
      .is("wp", "hasWord","wordId")
      .is("wordId", "hasStringValue", "word")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasPlayer", model.getPrefixedEntity("hasPlayer"))
      .addIriParam("hasWord", model.getPrefixedEntity("hasWord"))
      .addIriParam("hasStringValue", model.getPrefixedEntity("hasStringValue"))
      .addLiteralParam("id", id)
      .addLiteralParam("player", player)
      .addLiteralParam("word", word)
      .build();

    try {
      boolean result = ont.ask(q);
      return result;
    } catch (OntologyException e) {
      throw Exceptions.FailedToAskTheSolution(id, player, word, e);
    }

  }
}
