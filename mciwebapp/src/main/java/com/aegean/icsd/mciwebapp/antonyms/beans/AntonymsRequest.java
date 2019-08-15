package com.aegean.icsd.mciwebapp.antonyms.beans;

import com.aegean.icsd.mciwebapp.common.beans.Request;

public class AntonymsRequest extends Request {

  private String solution;

  public String getSolution() {
    return solution;
  }

  public void setSolution(String solution) {
    this.solution = solution;
  }
}
