package com.aegean.icsd.mciwebapp.observations.beans;

import java.util.List;

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
  private int level;

  @DataProperty("maxCompletionTime")
  private Long maxCompletionTime;

  @DataProperty("isCompletedIn")
  private String completionTime;

  @DataProperty("completedDate")
  private String completedDate;

  @DataProperty("hasTotalImages")
  private int totalImages;

  private List<String> imagePaths;

  private List<String> words;

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

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public String getCompletedDate() {
    return completedDate;
  }

  public void setCompletedDate(String completedDate) {
    this.completedDate = completedDate;
  }

  public int getTotalImages() {
    return totalImages;
  }

  public void setTotalImages(int totalImages) {
    this.totalImages = totalImages;
  }

  public List<String> getWords() {
    return words;
  }

  public void setWords(List<String> words) {
    this.words = words;
  }

  public List<String> getImagePaths() {
    return imagePaths;
  }

  public void setImagePaths(List<String> imagePaths) {
    this.imagePaths = imagePaths;
  }
}
