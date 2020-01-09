package com.aegean.icsd.engine.rules.implementations;

import com.aegean.icsd.engine.rules.beans.RulesException;

final class RulesExceptions {
  private static final String CODE_NAME = "GR";

  private RulesExceptions() { }

  static RulesException InvalidParameters() {
    return new RulesException(CODE_NAME + "." + 1, "The provided parameters are invalid. Please check that the " +
      "parameters are not null or empty");
  }

  static RulesException CannotRetrieveClassSchema(String gameName, Throwable t) {
    return new RulesException(CODE_NAME + "." + 2, String.format("Unable to retrieve rules for entity : %s", gameName), t);
  }

  static RulesException CannotFindProperty(String property, String entity) {
    return new RulesException(CODE_NAME + "." + 3, String.format("Unable to retrieve the requested property %s " +
      "for entity %s", property, entity));
  }

  static RulesException CannotFindEntityName(String entity) {
    return new RulesException(CODE_NAME + "." + 4, String.format("Unable to retrieve name ofr entity %s", entity));
  }

}