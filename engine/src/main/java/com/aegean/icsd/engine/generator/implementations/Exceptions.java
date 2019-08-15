package com.aegean.icsd.engine.generator.implementations;

import com.aegean.icsd.engine.common.beans.EngineException;

class Exceptions {
  private static final String CODE_NAME = "GG";

  private Exceptions () { }

  static EngineException CannotRetrieveRules(String entity, Throwable t) {
    return new EngineException(CODE_NAME + "." + 2, String.format("Unable to retrieve rules for entity: %s", entity), t);
  }

  static EngineException CannotCreateObjectRelation(String domainId, String relationName, String rangeId, String reason) {
    return new EngineException(CODE_NAME + "." + 3, String.format("There was an issue creating the object relation %s " +
      "between the domain with id %s and the range with id %s. More info: %s", relationName, domainId, rangeId, reason));
  }

  static EngineException CannotCreateRelation(String relationName, String gameId, Throwable t) {
    return new EngineException(CODE_NAME + "." + 4, String.format("There was an issue add the relation %s to the game with id %s", relationName, gameId), t);
  }

  static EngineException CannotCreateObject(String type) {
    return new EngineException(CODE_NAME + "." + 5, String.format("Could not create an object of type: %s", type));
  }

  static EngineException MissingMandatoryRelation(String type, String relationName) {
    return new EngineException(CODE_NAME + "." + 7, String.format("Missing mandatory relation %s from object %s", relationName, type));
  }

}
