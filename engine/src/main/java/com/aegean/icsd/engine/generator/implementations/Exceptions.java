package com.aegean.icsd.engine.generator.implementations;

import com.aegean.icsd.engine.generator.beans.GeneratorException;

class Exceptions {
  private static final String CODE_NAME = "GG";

  static GeneratorException InvalidParameters() {
    return new GeneratorException(CODE_NAME + "." + 1, "The provided parameters are invalid. Please check that the parameters are not null or empty");
  }

  static GeneratorException CannotRetrieveRules(String gameName, Throwable t) {
    return new GeneratorException(CODE_NAME + "." + 2, String.format("Unable to retrieve game rules for game: %s", gameName), t);
  }

}
