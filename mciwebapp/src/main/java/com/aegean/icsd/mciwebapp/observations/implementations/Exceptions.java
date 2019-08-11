package com.aegean.icsd.mciwebapp.observations.implementations;

import com.aegean.icsd.mciwebapp.common.beans.MciException;

class Exceptions {
  private static final String CODE_NAME = "OBS";

  static MciException InvalidRequest() {
    return new MciException(CODE_NAME + "." + 1, "The request is invalid. " +
      "Please check your request and retry");
  }

  static MciException GenerationError(Throwable t) {
    return new MciException(CODE_NAME + "." + 2, "There was a problem during the " +
      "generation of the game, please retry", t);
  }

  static MciException UnableToRetrieveGameRules(Throwable t) {
    return new MciException(CODE_NAME + "." + 3,
      "There was a problem retrieving the game rules", t);
  }

}
