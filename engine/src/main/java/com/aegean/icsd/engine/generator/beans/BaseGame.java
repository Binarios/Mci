package com.aegean.icsd.engine.generator.beans;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Id;
import com.aegean.icsd.engine.core.annotations.Key;

public class BaseGame {
  @Id
  @DataProperty("hasId")
  private String id;

  @Key
  @DataProperty("hasDifficulty")
  private Difficulty difficulty;

  @Key
  @DataProperty("hasPlayer")
  private String playerName;

  @Key
  @DataProperty("hasLevel")
  private Integer level;

  @DataProperty("maxCompletionTime")
  private Long maxCompletionTime;

  @DataProperty("isCompletedIn")
  private Long completionTime;

  @DataProperty("completedDate")
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

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public Long getMaxCompletionTime() {
    return maxCompletionTime;
  }

  public void setMaxCompletionTime(Long maxCompletionTime) {
    this.maxCompletionTime = maxCompletionTime;
  }

  public Long getCompletionTime() {
    return completionTime;
  }

  public void setCompletionTime(Long completionTime) {
    this.completionTime = completionTime;
  }

  public String getCompletedDate() {
    return completedDate;
  }

  public void setCompletedDate(String completedDate) {
    this.completedDate = completedDate;
  }
}
