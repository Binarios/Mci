package com.aegean.icsd.engine.generator.implementations;

import com.aegean.icsd.engine.common.beans.EngineException;

class Exceptions {
  private static final String CODE_NAME = "GG";

  static EngineException InvalidParameters() {
    return new EngineException(CODE_NAME + "." + 1, "The provided parameters are invalid. Please check that the parameters are not null or empty");
  }

  static EngineException CannotRetrieveRules(String gameName, Throwable t) {
    return new EngineException(CODE_NAME + "." + 2, String.format("Unable to retrieve game rules for game: %s", gameName), t);
  }

  static EngineException CannotCreateCoreGame(String gameName, Throwable t) {
    return new EngineException(CODE_NAME + "." + 3, String.format("There was an issue creating a core game %s", gameName), t);
  }

  static EngineException CannotCreateRelation(String relationName, String gameId, Throwable t) {
    return new EngineException(CODE_NAME + "." + 3, String.format("There was an issue add the relation %s to the game with id %s", relationName, gameId), t);
  }

  static EngineException CannotCreateObject(String type) {
    return new EngineException(CODE_NAME + "." + 4, String.format("Could not create an object of type: %s", type));
  }
}
