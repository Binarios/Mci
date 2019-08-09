package com.aegean.icsd.mciwebapp.observations.dao;

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
}
