package com.aegean.icsd.mciwebapp.puzzles.beans;

import java.util.List;
import java.util.Map;

import com.aegean.icsd.mciwebapp.common.beans.Request;

public class PuzzleRequest extends Request {
  private Map<String, List<String>> solution;

  public Map<String, List<String>> getSolution() {
    return solution;
  }

  public void setSolution(Map<String, List<String>> solution) {
    this.solution = solution;
  }
}
