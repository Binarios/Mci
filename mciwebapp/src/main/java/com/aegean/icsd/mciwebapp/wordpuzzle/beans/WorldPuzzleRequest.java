package com.aegean.icsd.mciwebapp.wordpuzzle.beans;

public class WorldPuzzleRequest {
  private String difficulty;

  private Long completionTime;

  private String solution;

  public String getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
  }

  public Long getCompletionTime() {
    return completionTime;
  }

  public void setCompletionTime(Long completionTime) {
    this.completionTime = completionTime;
  }

  public String getSolution() {
    return solution;
  }

  public void setSolution(String solution) {
    this.solution = solution;
  }
}
