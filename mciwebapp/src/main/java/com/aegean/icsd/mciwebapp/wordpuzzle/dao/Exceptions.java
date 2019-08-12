package com.aegean.icsd.mciwebapp.wordpuzzle.dao;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.ontology.beans.OntologyException;

class Exceptions {
  private static final String CODE_NAME = "OBS.DAO";

  static MciException GenerationError(Throwable t) {
    return new MciException(CODE_NAME + "." + 1, "There was a problem during the " +
      "generation of the game, please retry", t);
  }

  public static MciException FailedToRetrieveLastLevel(String gameName, Difficulty difficulty,
                                                                String playerName, OntologyException e) {
    return new MciException(CODE_NAME + "." + 3, String.format("Unable to retrieve the last completed level for" +
      " the game \"%s\" with difficulty \"%s\" and player \"%s\"", gameName, difficulty.name(), playerName)
      , e);
  }

  public static MciException FailedToRetrieveGames(String player, Throwable e) {
    return new MciException(CODE_NAME + "." + 5, String.format("Could not retrieve the games of player %s ", player), e);
  }

  public static MciException FailedToAskTheSolution(String id, String player, String word, Integer occurrences, Throwable e) {
    return new MciException(CODE_NAME + "." + 7,
      String.format("Failed to ask the solution of the game with id %s for player %s. " +
        "The asked word was %s with occurences %s ", id, player, word, occurrences), e);
  }
}
