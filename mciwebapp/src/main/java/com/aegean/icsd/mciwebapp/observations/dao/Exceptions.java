package com.aegean.icsd.mciwebapp.observations.dao;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;

class Exceptions {
  private static final String CODE_NAME = "OBS.DAO";

  static ObservationsException GenerationError(Throwable t) {
    return new ObservationsException(CODE_NAME + "." + 1, "There was a problem during the " +
      "generation of the game, please retry", t);
  }

}
