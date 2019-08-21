package com.aegean.icsd.mciwebapp.findthesounds.beans;

import com.aegean.icsd.mciwebapp.common.beans.Request;

public class FindTheSoundRequest extends Request {
  private Solution solution;

  public Solution getSolution() {
    return solution;
  }

  public void setSolution(Solution solution) {
    this.solution = solution;
  }
}
