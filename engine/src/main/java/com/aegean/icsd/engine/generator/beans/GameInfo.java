package com.aegean.icsd.engine.generator.beans;


public class GameInfo {
  private String id;
  private String maxCompletionTime;
  private String playerName;
  private String level;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMaxCompletionTime() {
    return maxCompletionTime;
  }

  public void setMaxCompletionTime(String maxCompletionTime) {
    this.maxCompletionTime = maxCompletionTime;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }
}
