package com.aegean.icsd.mciwebapp.observations.beans;

import com.aegean.icsd.engine.common.beans.Difficulty;

public class Observation {
  private String id;
  private Difficulty difficulty;
  private String playerName;
  private String maxCompletionTime;
  private String completionTime;
  private String level;
  private String completedDate;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public String getMaxCompletionTime() {
    return maxCompletionTime;
  }

  public void setMaxCompletionTime(String maxCompletionTime) {
    this.maxCompletionTime = maxCompletionTime;
  }

  public String getCompletionTime() {
    return completionTime;
  }

  public void setCompletionTime(String completionTime) {
    this.completionTime = completionTime;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getCompletedDate() {
    return completedDate;
  }

  public void setCompletedDate(String completedDate) {
    this.completedDate = completedDate;
  }

}
