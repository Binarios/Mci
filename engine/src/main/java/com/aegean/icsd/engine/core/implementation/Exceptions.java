package com.aegean.icsd.engine.core.implementation;

import com.aegean.icsd.engine.common.beans.EngineException;

class Exceptions {
  private static final String CODE_NAME = "ARD";

  static EngineException UnableToReadAnnotation(String annotation) {
    return new EngineException(CODE_NAME + "." + 1, String.format("Annotation <%s> was not found in the provided bean.", annotation));
  }

  static EngineException GenericError(Throwable t) {
    return new EngineException(CODE_NAME + "." + 100, "There was a generic error. See including trace for more details", t);
  }
}
