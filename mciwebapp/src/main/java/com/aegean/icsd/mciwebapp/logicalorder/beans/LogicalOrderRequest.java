package com.aegean.icsd.mciwebapp.logicalorder.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.Request;

public class LogicalOrderRequest extends Request {
  private List<SolutionItem> solution;

  public List<SolutionItem> getSolution() {
    return solution;
  }

  public void setSolution(List<SolutionItem> solution) {
    this.solution = solution;
  }
}
