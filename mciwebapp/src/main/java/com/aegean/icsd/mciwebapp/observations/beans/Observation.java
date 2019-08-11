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

  @Key
  @DataProperty("hasLevel")
  private Integer level;

  @DataProperty("maxCompletionTime")
  private Long maxCompletionTime;

  @DataProperty("isCompletedIn")
  private Long completionTime;

  @DataProperty("completedDate")
  private String completedDate;

  @DataProperty("hasTotalImages")
  private Integer totalImages;

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

  public Long getCompletionTime() {
    return completionTime;
  }

  public void setCompletionTime(Long completionTime) {
    this.completionTime = completionTime;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public String getCompletedDate() {
    return completedDate;
  }

  public void setCompletedDate(String completedDate) {
    this.completedDate = completedDate;
  }

  public Integer getTotalImages() {
    return totalImages;
  }

  public void setTotalImages(Integer totalImages) {
    this.totalImages = totalImages;
  }
}
