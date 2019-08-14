package com.aegean.icsd.mciwebapp.common.beans;

public class Request {
  private String difficulty;
  private Long completionTime;

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
}
