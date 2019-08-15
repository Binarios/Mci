package com.aegean.icsd.mciwebapp.synonym.dao;

import com.aegean.icsd.mciwebapp.common.beans.MciException;

final class Exceptions {
  private static final String CODE_NAME = "SYNONYM.DAO";

  private Exceptions () { }

  static MciException UnableToFindMainWord(String id, Throwable e) {
    return new MciException(CODE_NAME + "." + 1,
      String.format("Unable to retrieve the main word for game with id %s ", id), e);
  }
}
