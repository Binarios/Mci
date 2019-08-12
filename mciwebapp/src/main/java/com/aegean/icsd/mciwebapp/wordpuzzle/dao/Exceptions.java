package com.aegean.icsd.mciwebapp.wordpuzzle.dao;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.ontology.beans.OntologyException;

class Exceptions {
  private static final String CODE_NAME = "WORDPUZZLE.DAO";

  public static MciException FailedToRetrieveWord(String id, OntologyException e) {
    return new MciException(CODE_NAME + "." + 1, String.format("There was a problem when retrieving the word associated to the" +
      "id %s.",id), e);
  }
}
