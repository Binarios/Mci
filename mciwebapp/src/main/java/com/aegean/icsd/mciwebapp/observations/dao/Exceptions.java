package com.aegean.icsd.mciwebapp.observations.dao;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;
import com.aegean.icsd.ontology.beans.OntologyException;

class Exceptions {
  private static final String CODE_NAME = "OBS.DAO";

  static ObservationsException GenerationError(Throwable t) {
    return new ObservationsException(CODE_NAME + "." + 1, "There was a problem during the " +
      "generation of the game, please retry", t);
  }

  public static ObservationsException FailedToRetrieveWords(String id, OntologyException e) {
    return new ObservationsException(CODE_NAME + "." + 2, String.format("There was a problem when retrieving the words associated to the" +
      "id %s.",id), e);
  }

  public static ObservationsException FailedToRetrieveLastLevel(String gameName, Difficulty difficulty,
                                                                String playerName, OntologyException e) {
    return new ObservationsException(CODE_NAME + "." + 3, String.format("Unable to retrieve the last completed level for" +
      " the game \"%s\" with difficulty \"%s\" and player \"%s\"", gameName, difficulty.name(), playerName)
      , e);
  }

  public static ObservationsException FailedToRetrievePaths(String id, OntologyException e) {
    return new ObservationsException(CODE_NAME + "." + 4, String.format("There was a problem when retrieving the images associated to the " +
      "id %s.",id), e);
  }
}
