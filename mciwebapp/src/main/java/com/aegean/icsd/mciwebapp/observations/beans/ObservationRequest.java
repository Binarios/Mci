package com.aegean.icsd.mciwebapp.observations.beans;

import java.util.Map;

public class ObservationRequest {
  private String difficulty;

  private Map<String, Integer> solution;

  private Long completionTime;

  public String getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
  }

  public Map<String, Integer> getSolution() {
    return solution;
  }

  public void setSolution(Map<String, Integer> solution) {
    this.solution = solution;
  }

  public Long getCompletionTime() {
    return completionTime;
  }

  public void setCompletionTime(Long completionTime) {
    this.completionTime = completionTime;
  }
}
