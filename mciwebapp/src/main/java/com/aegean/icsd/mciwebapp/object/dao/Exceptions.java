package com.aegean.icsd.mciwebapp.object.dao;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

class Exceptions {
  private static final String CODE_NAME = "OBJECTS.DAO";

  public static ProviderException FailedToRetrieveWords(Throwable t) {
    return new ProviderException(CODE_NAME + "." + 1, "There was a problem when a word object", t);
  }
}
