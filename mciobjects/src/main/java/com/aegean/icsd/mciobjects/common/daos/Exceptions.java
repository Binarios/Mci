package com.aegean.icsd.mciobjects.common.daos;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;

public final class Exceptions {
  private static final String CODE_NAME = "OBJECTS.DAO";

  private Exceptions() { }

  public static ProviderException FailedToRetrieveObjects(String entityName, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 1, String.format("There was a problem when retrieving %s", entityName), t);
  }

  public static ProviderException FailedToRetrieveObjects(String entityName) {
    return new ProviderException(CODE_NAME + "." + 2, String.format("There was a problem when retrieving %s", entityName));
  }

  public static ProviderException FailedToAsk (String question, Throwable e) {
    return new ProviderException(CODE_NAME + "." + 3, String.format("Failed to ask: %s ", question), e);
  }
}
