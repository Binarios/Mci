package com.aegean.icsd.mciwebapp.observations.implementations;

import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;

class Exceptions {
  private static final String CODE_NAME = "OBS";

  static ObservationsException InvalidRequest() {
    return new ObservationsException(CODE_NAME + "." + 1, "The request is invalid. " +
      "Please check your request and retry");
  }

  static ObservationsException GenerationError(Throwable t) {
    return new ObservationsException(CODE_NAME + "." + 2, "There was a problem during the " +
      "generation of the game, please retry", t);
  }

  static ObservationsException UnableToRetrieveGameRules(Throwable t) {
    return new ObservationsException(CODE_NAME + "." + 3,
      "There was a problem retrieving the game rules", t);
  }

  static ObservationsException GenerationError(String msg) {
    return new ObservationsException(CODE_NAME + "." + 4, String.format("There was a problem during the " +
      "generation of the game: %s", msg));
  }
  static ObservationsException CannotCalculateCardinality(String entity) {
    return new ObservationsException(CODE_NAME + "." + 5,
      String.format( "Unable to calculate the cardinality of the entity: %s", entity));
  }

}
