package com.aegean.icsd.engine.generator.beans;

import java.util.UUID;

public class GameInfo {
  private UUID id;
  private String maxCompletionTime;
  private String playerName;
  private String level;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
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
