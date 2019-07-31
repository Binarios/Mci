package com.aegean.icsd.mciwebapp.observations.implementations;

import com.aegean.icsd.engine.common.beans.Difficulty;
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

  static ObservationsException UnableToRetrieveMaxCompletionTime(Difficulty difficulty, Throwable t) {
    return new ObservationsException(CODE_NAME + "." + 3,
      String.format("There was a problem retrieving the maximum completion" +
        "time for difficulty %s, please retry", difficulty.getNormalizedName()), t);
  }
}
