package com.aegean.icsd.mciwebapp.wordpuzzle.dao;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.ontology.beans.OntologyException;

class Exceptions {
  private static final String CODE_NAME = "WORDPUZZLE.DAO";

  public static MciException FailedToRetrieveWord(String id, Throwable e) {
    return new MciException(CODE_NAME + "." + 1, String.format("There was a problem when retrieving the word associated to the" +
      "id %s.",id), e);
  }

  public static MciException FailedToAskTheSolution(String id, String player, String word, Throwable e) {
    return new MciException(CODE_NAME + "." + 7,
      String.format("Failed to ask the solution of the game with id %s for player %s. " +
        "The asked word was %s ", id, player, word), e);
  }
}
