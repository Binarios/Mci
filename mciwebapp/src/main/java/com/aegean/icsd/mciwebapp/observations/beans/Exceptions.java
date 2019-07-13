package com.aegean.icsd.mciwebapp.observations.beans;

public class Exceptions {
  private static final String CODE_NAME = "OBS";

  public static ObservationsException InvalidRequest() {
    return new ObservationsException(CODE_NAME + "." + 1, "The request is invalid. Please check your request and retry");
  }

  public static ObservationsException GenerationError(Throwable t) {
    return new ObservationsException(CODE_NAME + "." + 2, "There was a problem during the generation of the game, please retry", t);
  }
}
