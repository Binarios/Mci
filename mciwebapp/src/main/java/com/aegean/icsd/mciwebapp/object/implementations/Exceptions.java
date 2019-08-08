package com.aegean.icsd.mciwebapp.object.implementations;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

class Exceptions {
  private final static String CODE_NAME = "OP";

  static ProviderException UnableToReadFile(String path, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 1, String.format("Unable to locate the requested filePath: %s", path), t);
  }

  static ProviderException UnableToReadFile(String path) {
    return new ProviderException(CODE_NAME + "." + 2, String.format("Unable to locate the requested filePath: %s", path));
  }

  static ProviderException GenerationError(Throwable t) {
    return new ProviderException(CODE_NAME + "." + 3, "There was a problem during the " +
      "generation of the word, please retry", t);
  }

  static ProviderException UnableToRetrieveRules(String entity, Throwable t) {
    return new ProviderException(CODE_NAME + "." + 4,
      String.format("There was a problem retrieving the rules of %s", entity), t);
  }

  static ProviderException UnableToFindObjectProvider(String object) {
    return new ProviderException(CODE_NAME + "." + 5,
      String.format("Cannot find the object provider for the object %s", object));
  }
}
