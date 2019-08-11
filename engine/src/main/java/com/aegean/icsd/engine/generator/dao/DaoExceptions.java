package com.aegean.icsd.engine.generator.dao;

import com.aegean.icsd.engine.common.beans.EngineException;

class DaoExceptions {

  private static final String CODE_NAME = "GG.DAO";

  static EngineException SelectQuery(String extraMsg, Throwable t) {
    return new EngineException(CODE_NAME + "." + 1, String.format("There was a problem when executing a select. More details: %s", extraMsg), t);
  }

  static EngineException InsertQuery(String extraMsg, Throwable t) {
    return new EngineException(CODE_NAME + "." + 2, String.format("There was a problem when inserting an entry. More details: %s", extraMsg), t);
  }

  static EngineException UnableToReadAnnotation(String annotation) {
    return new EngineException(CODE_NAME + "." + 3, String.format("Annotation <%s> was not found in the provided bean.", annotation));
  }

  static EngineException GenericError(Throwable t) {
    return new EngineException(CODE_NAME + "." + 100, "There was a generic error. See including trace for more details", t);
  }
}
