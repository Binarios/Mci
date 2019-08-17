package com.aegean.icsd.mciwebapp.object.dao;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

final class Exceptions {
  private static final String CODE_NAME = "OBJECTS.DAO";

  private Exceptions () { }

  static ProviderException FailedToRetrieveObjects(String entityName, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 1, String.format("There was a problem when retrieving %s", entityName), t);
  }

  static ProviderException FailedToRetrieveObjects(String entityName) {
    return new ProviderException(CODE_NAME + "." + 2, String.format("There was a problem when retrieving %s", entityName));
  }

  static ProviderException FailedToAsk (String question, Throwable e) {
    return new ProviderException(CODE_NAME + "." + 3, String.format("Failed to ask: %s ", question), e);
  }
}
