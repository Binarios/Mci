package com.aegean.icsd.engine.generator.implementation;

import com.aegean.icsd.engine.common.beans.EngineException;

class Exceptions {
  private static final String CODE_NAME = "GG";

  static EngineException InvalidParameters() {
    return new EngineException(CODE_NAME + "." + 1, "The provided parameters are invalid. Please check that the parameters are not null or empty");
  }

  static EngineException CannotRetrieveRules(String gameName, Throwable t) {
    return new EngineException(CODE_NAME + "." + 2, String.format("Unable to retrieve game rules for game: %s", gameName), t);
  }

}
