package com.aegean.icsd.mciwebapp.object.dao;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

class Exceptions {
  private static final String CODE_NAME = "OBJECTS.DAO";

  public static ProviderException FailedToRetrieveWords(String entityName, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 1, String.format("There was a problem when retrieving %s", entityName), t);
  }
}
