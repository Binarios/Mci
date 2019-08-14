package com.aegean.icsd.mciwebapp.synonims.beans;

import com.aegean.icsd.mciwebapp.common.beans.Request;

public class SynonymRequest extends Request {

  private String solution;

  public String getSolution() {
    return solution;
  }

  public void setSolution(String solution) {
    this.solution = solution;
  }
}
