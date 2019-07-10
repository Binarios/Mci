package com.aegean.icsd.mciwebapp.observations.beans;

public class Exceptions {
  private static final String CODE_NAME = "OBS";

  public static ObservationsException InvalidRequest() {
    return new ObservationsException(CODE_NAME + "." + 1, "The request is invalid. Please check your request and retry");
  }

}
