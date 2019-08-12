package com.aegean.icsd.mciwebapp.observations.dao;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.ontology.beans.OntologyException;

class Exceptions {
  private static final String CODE_NAME = "OBS.DAO";

  public static MciException FailedToRetrieveWords(String id, OntologyException e) {
    return new MciException(CODE_NAME + "." + 2, String.format("There was a problem when retrieving the words associated to the" +
      "id %s.",id), e);
  }


  public static MciException FailedToRetrievePaths(String id, OntologyException e) {
    return new MciException(CODE_NAME + "." + 4, String.format("There was a problem when retrieving the images associated to the " +
      "id %s.",id), e);
  }

  public static MciException FailedToRetrieveObservationItems(String id, Throwable e) {
    return new MciException(CODE_NAME + "." + 6, String.format("Could not retrieve the items of game with id %s ", id), e);
  }

  public static MciException FailedToAskTheSolution(String id, String player, String word, Integer occurrences, Throwable e) {
    return new MciException(CODE_NAME + "." + 7,
      String.format("Failed to ask the solution of the game with id %s for player %s. " +
        "The asked word was %s with occurences %s ", id, player, word, occurrences), e);
  }
}
