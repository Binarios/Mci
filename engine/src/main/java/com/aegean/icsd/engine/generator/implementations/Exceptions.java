package com.aegean.icsd.engine.generator.implementations;

import com.aegean.icsd.engine.common.beans.EngineException;

class Exceptions {
  private static final String CODE_NAME = "GG";

  static EngineException InvalidParameters() {
    return new EngineException(CODE_NAME + "." + 1, "The provided parameters are invalid. Please check that the parameters are not null or empty");
  }

  static EngineException CannotRetrieveRules(String entity, Throwable t) {
    return new EngineException(CODE_NAME + "." + 2, String.format("Unable to retrieve rules for entity: %s", entity), t);
  }

  static EngineException CannotCreateCoreGame(String gameName, Throwable t) {
    return new EngineException(CODE_NAME + "." + 3, String.format("There was an issue creating a core game %s", gameName), t);
  }

  static EngineException CannotCreateRelation(String relationName, String gameId, Throwable t) {
    return new EngineException(CODE_NAME + "." + 4, String.format("There was an issue add the relation %s to the game with id %s", relationName, gameId), t);
  }

  static EngineException CannotCreateObject(String type) {
    return new EngineException(CODE_NAME + "." + 5, String.format("Could not create an object of type: %s", type));
  }

  static EngineException UnableToRetrieveDataProperties(Throwable t) {
    return new EngineException(CODE_NAME + "." + 6, "Could not retrieve the data properties", t);
  }

  static EngineException MissingMandatoryRelation(String type, String relationName) {
    return new EngineException(CODE_NAME + "." + 7, String.format("Missing mandatory relation %s from object %s", relationName, type));
  }

  static EngineException ConstructorNotFound(String gameName, Throwable t) {
    return new EngineException(CODE_NAME + "." + 8, String.format("Could not find class for game %s", gameName), t);
  }

  static EngineException GenericError(Throwable t) {
    return new EngineException(CODE_NAME + "." + 100, "There was a generic error. See including trace for more details", t);
  }
}
