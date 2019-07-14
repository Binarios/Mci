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
}
