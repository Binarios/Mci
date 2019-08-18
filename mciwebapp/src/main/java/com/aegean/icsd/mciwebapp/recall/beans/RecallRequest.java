package com.aegean.icsd.mciwebapp.recall.beans;

import com.aegean.icsd.mciwebapp.common.beans.Request;

public class RecallRequest extends Request {
  private Long solution;

  public Long getSolution() {
    return solution;
  }

  public void setSolution(Long solution) {
    this.solution = solution;
  }
}
