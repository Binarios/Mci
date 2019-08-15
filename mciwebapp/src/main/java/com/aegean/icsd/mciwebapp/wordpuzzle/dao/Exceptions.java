package com.aegean.icsd.mciwebapp.wordpuzzle.dao;

import com.aegean.icsd.mciwebapp.common.beans.MciException;

final class Exceptions {
  private static final String CODE_NAME = "WORDPUZZLE.DAO";

  private Exceptions () { }

  static MciException FailedToAskTheSolution(String id, String player, String word, Throwable e) {
    return new MciException(CODE_NAME + "." + 7,
      String.format("Failed to ask the solution of the game with id %s for player %s. " +
        "The asked word was %s ", id, player, word), e);
  }
}
