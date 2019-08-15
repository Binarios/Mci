package com.aegean.icsd.mciwebapp.common;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public class GameExceptions {

  public static MciException InvalidRequest(String game) {
    return new MciException(game + "." + 1, String.format("The request is invalid (%s). " +
      "Please check your request and retry", game));
  }

  public static MciException GenerationError(String game, Throwable t) {
    return new MciException(game + "." + 2, String.format("There was a problem during the " +
      "generation of the game %s, please retry", game), t);
  }

  public static MciException UnableToRetrieveGameRules(String game, Throwable t) {
    return new MciException(game + "." + 3,
      "There was a problem retrieving the game rules", t);
  }

  public static MciException UnableToRetrieveGame(String game, String id, String player) {
    return new MciException(game + "." + 4,
      String.format("Game with id %s and player %s doesn't exist", id, player));
  }

  public static MciException GameIsAlreadySolvedAt(String game, String id, String date) {
    return new MciException(game + "." + 5,
      String.format("Game with id %s has already been solved at %s", id, date));
  }

  public static MciException SurpassedMaxCompletionTime(String game, String id, Long maxCompletionTime) {
    return new MciException(game + "." + 6,
      String.format("The completion time of game with id %s is greater than the allowed of %s", id, maxCompletionTime));
  }

  public static MciException FailedToRetrieveGames(String game, String player, Throwable e) {
    return new MciException(game + "." + 7, String.format("Could not retrieve the games of player %s ", player), e);
  }

  public static MciException FailedToRetrieveLastLevel(String game, Difficulty difficulty,
                                                String playerName, Throwable t) {
    return new MciException(game + "." + 8, String.format("Unable to retrieve the last completed level for" +
      " the game \"%s\" with difficulty \"%s\" and player \"%s\"", game, difficulty.name(), playerName)
      , t);
  }

  public static MciException FailedToRetrieveWord(String game, String id) {
    return new MciException(game + "." + 9, String.format("Could not retrieve the word associated with the id %s ", id));
  }

  public static MciException UnableToSolve(String game, Throwable t) {
    return new MciException(game + "." + 10, String.format("Unable to solve game %s", game), t);
  }

  public static MciException UnableToSolve(String game, String info) {
    return new MciException(game + "." + 11, String.format("Unable to solve game %s, %s", game, info));
  }


  public static MciException GenerationError(String game, String msg) {
    return new MciException(game + "." + 99, String.format("There was a problem during the " +
      "generation of the game %s. %s", game, msg));
  }
}
