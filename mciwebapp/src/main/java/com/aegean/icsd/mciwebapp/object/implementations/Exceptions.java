package com.aegean.icsd.mciwebapp.object.implementations;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

class Exceptions {
  private final static String CODE_NAME = "OP";

  static ProviderException UnableToGetWord(String criteria, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 1, String.format("Unable to get the word with the associated criteria: %s", criteria), t);
  }

  static ProviderException UnableToReadFile(String path) {
    return new ProviderException(CODE_NAME + "." + 2, String.format("Unable to locate the requested filePath: %s", path));
  }

  static ProviderException GenerationError(String entity, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 3, String.format("There was a problem during the " +
      "generation of the %s, please retry", entity), t);
  }

  static ProviderException UnableToRetrieveRules(String entity, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 4,
      String.format("There was a problem retrieving the rules of %s", entity), t);
  }

  static ProviderException UnableToGetFileFromUrl(String url, String file, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 5,
      String.format("Unable to get the requested file: %s from the url: %s", file, url), t);
  }

  static ProviderException UnableToGenerateObject(String name) {
    return new ProviderException(CODE_NAME + "." + 6,
      String.format("Unable to generate a new %s", name));
  }
}
