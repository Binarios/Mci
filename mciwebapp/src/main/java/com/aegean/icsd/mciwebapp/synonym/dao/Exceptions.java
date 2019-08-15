package com.aegean.icsd.mciwebapp.synonym.dao;

import com.aegean.icsd.mciwebapp.common.beans.MciException;

class Exceptions {
  private static final String CODE_NAME = "SYNONYM.DAO";

  public static MciException FailedToAskTheSolution(String id, String player, String word, Throwable e) {
    return new MciException(CODE_NAME + "." + 1,
      String.format("Failed to ask the solution of the game with id %s for player %s. " +
        "The asked word was %s ", id, player, word), e);
  }

  public static MciException UnableToFindMainWord(String id, Throwable e) {
    return new MciException(CODE_NAME + "." + 2,
      String.format("Unable to retrieve the main word for game with id %s ", id), e);
  }

  public static MciException UnableToFindWord(String id, String wordId, Throwable e) {
    return new MciException(CODE_NAME + "." + 3,
      String.format("Unable to retrieve the word with id %s for game with id %s ", wordId, id), e);
  }
}
