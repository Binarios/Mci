package com.aegean.icsd.mciwebapp.observations.beans;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Id;
import com.aegean.icsd.engine.core.annotations.Key;

@Entity(Observation.NAME)
public class Observation {
  public static final String NAME = "Observation";

  @Id
  @DataProperty("hasId")
  private String id;

  @Key
  @DataProperty("hasDifficulty")
  private Difficulty difficulty;

  @Key
  @DataProperty("hasPlayer")
  private String playerName;

  @DataProperty("maxCompletionTime")
  private Long maxCompletionTime;

  @DataProperty("isCompletedIn")
  private String completionTime;

  @DataProperty("hasLevel")
  private String level;

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

  public Long getMaxCompletionTime() {
    return maxCompletionTime;
  }

  public void setMaxCompletionTime(Long maxCompletionTime) {
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
