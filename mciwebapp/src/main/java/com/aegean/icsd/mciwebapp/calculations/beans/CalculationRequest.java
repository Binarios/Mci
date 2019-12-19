package com.aegean.icsd.mciwebapp.calculations.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.Request;

public class CalculationRequest extends Request {

  private List<BlockItem> solution;

  public List<BlockItem> getSolution() {
    return solution;
  }

  public void setSolution(List<BlockItem> solution) {
    this.solution = solution;
  }
}
