package com.aegean.icsd.engine.generator.dao;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;

final class DaoExceptions {

  private static final String CODE_NAME = "GG.DAO";

  private DaoExceptions() { }

  static EngineException InsertQuery(String extraMsg, Throwable t) {
    return new EngineException(CODE_NAME + "." + 2, String.format("There was a problem when inserting an entry. More details: %s", extraMsg), t);
  }

  static EngineException SelectObjectQuery(String extraMsg) {
    return new EngineException(CODE_NAME + "." + 3, String.format("There was a problem when selecting the relations of a Game Object. More details: %s", extraMsg));
  }

  static EngineException SelectObjectQuery(String extraMsg, Throwable t) {
    return new EngineException(CODE_NAME + "." + 3, String.format("There was a problem when selecting the relations of a Game Object. More details: %s", extraMsg), t);
  }


  static EngineException FailedToRetrieveLastLevel(String gameName, Difficulty difficulty,
      String playerName, Throwable t) {
    return new EngineException(CODE_NAME + "." + 4, String.format("Unable to retrieve the last completed level for" +
        " the game \"%s\" with difficulty \"%s\" and player \"%s\"", gameName, difficulty.name(), playerName)
        , t);
  }

  static EngineException FailedToRetrieveGames(String player, Throwable e) {
    return new EngineException(CODE_NAME + "." + 5, String.format("Could not retrieve the games of player %s ", player), e);
  }

  static EngineException FailedToRetrieveGames(String player) {
    return new EngineException(CODE_NAME + "." + 5, String.format("Could not retrieve the games of player %s ", player));
  }

  static EngineException ConstructorNotFound(String gameName, Throwable t) {
    return new EngineException(CODE_NAME + "." + 8, String.format("Could not find class for game %s", gameName), t);
  }
}
