package com.aegean.icsd.mciobjects.common.implementations;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;

public final class ProviderExceptions {
  private final static String CODE_NAME = "OP";

  private ProviderExceptions() { }

  public static ProviderException UnableToReadFile(String path) {
    return new ProviderException(CODE_NAME + "." + 1, String.format("Unable to locate the requested filePath: %s", path));
  }

  public static ProviderException GenerationError(String entity, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 2, String.format("There was a problem during the " +
      "generation of the %s, please retry", entity), t);
  }

  public static ProviderException UnableToRetrieveRules(String entity, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 3,
      String.format("There was a problem retrieving the rules of %s", entity), t);
  }

  public static ProviderException UnableToGetFileFromUrl(String url, String file, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 4,
      String.format("Unable to get the requested file: %s from the url: %s", file, url), t);
  }

  public static ProviderException UnableToGenerateObject(String name) {
    return new ProviderException(CODE_NAME + "." + 5,
      String.format("Unable to generate a new %s", name));
  }

  public static ProviderException UnableToGetObject(String message, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 6, String.format("Unable to get object with the associated criteria: %s", message), t);
  }

  public static ProviderException UnableToGetObject(String message) {
    return new ProviderException(CODE_NAME + "." + 7, String.format("Unable to get object with the associated criteria: %s", message));
  }

}
