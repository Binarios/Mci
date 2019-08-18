package com.aegean.icsd.mciwebapp.numberorder.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.Request;

public class NumberOrderRequest extends Request {

  private List<SolutionItem> solution;

  public List<SolutionItem> getSolution() {
    return solution;
  }

  public void setSolution(List<SolutionItem> solution) {
    this.solution = solution;
  }
}
