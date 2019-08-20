package com.aegean.icsd.mciwebapp.memorycards.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.Request;

public class MemoryCardsRequest extends Request {
  private List<String> solution;

  public List<String> getSolution() {
    return solution;
  }

  public void setSolution(List<String> solution) {
    this.solution = solution;
  }
}
