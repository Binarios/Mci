package com.aegean.icsd.mciwebapp.providers.implementations;

import com.aegean.icsd.mciwebapp.providers.beans.ProviderException;

class Exceptions {
  private final static String code = "VP";

  static ProviderException UnableToReadFile(String path, Throwable t) {
    return new ProviderException(code + "." + 1, String.format("Unable to locate the requested filePath: %s", path), t);
  }
}
