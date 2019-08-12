package com.aegean.icsd.mciwebapp.wordpuzzle.implementations;

import com.aegean.icsd.mciwebapp.common.beans.MciException;

class Exceptions {
  private static final String CODE_NAME = "WOR_PUZ";

  static MciException InvalidRequest() {
    return new MciException(CODE_NAME + "." + 1, "The request is invalid. " +
      "Please check your request and retry");
  }

  static MciException GenerationError(Throwable t) {
    return new MciException(CODE_NAME + "." + 2, "There was a problem during the " +
      "generation of the game, please retry", t);
  }

  static MciException UnableToRetrieveGameRules(Throwable t) {
    return new MciException(CODE_NAME + "." + 3,
      "There was a problem retrieving the game rules", t);
  }

  static MciException UnableToRetrieveGame(String id, String player) {
    return new MciException(CODE_NAME + "." + 4,
     String.format("Game with id %s and player %s doesn't exist", id, player));
  }

  static MciException GameIsAlreadySolvedAt(String id, String date) {
    return new MciException(CODE_NAME + "." + 5,
      String.format("Game with id %s has already been solved at %s", id, date));
  }

  static MciException SurpassedMaxCompletionTime(String id, Long maxCompletionTime) {
    return new MciException(CODE_NAME + "." + 5,
      String.format("The completion time of game with id %s is greater than the allowed of %s", id, maxCompletionTime));
  }
}
