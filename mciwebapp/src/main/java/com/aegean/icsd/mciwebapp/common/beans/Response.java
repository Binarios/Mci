package com.aegean.icsd.mciwebapp.common.beans;

public class Response<T> {
  private T payload;
  private AppError error;

  public Response(T payload) {
    this.payload = payload;
  }

  public AppError getError() {
    return error;
  }

  public void setError(AppError appError) {
    this.error = appError;
  }
}
