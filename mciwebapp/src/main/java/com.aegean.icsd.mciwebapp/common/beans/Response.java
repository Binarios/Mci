package com.aegean.icsd.mciwebapp.common.beans;

import java.util.List;

public class Response<T> {
  private T payload;
  private List<Error> errors;

  public Response(T payload) {
    this.payload = payload;
  }

  public Response(List<Error> errors) {
    this.errors = errors;
  }
}
