package com.aegean.icsd.engine.rules.implementations;

import com.aegean.icsd.engine.rules.beans.RulesException;

final class Exceptions {
  private static final String CODE_NAME = "GR";

  static RulesException InvalidParameters() {
    return new RulesException(CODE_NAME + "." + 1, "The provided parameters are invalid. Please check that the parameters are not null or empty");
  }

  static RulesException CannotRetrieveClassSchema(String gameName, Throwable t) {
    return new RulesException(CODE_NAME + "." + 2, String.format("Unable to retrieve game rules for game: %s", gameName), t);
  }

  static RulesException CannotFindRestriction(String restriction, String gameName) {
    return new RulesException(CODE_NAME + "." + 3, String.format("Unable to retrieve the requested restriction %s for game %s", restriction, gameName));
  }

}