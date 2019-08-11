package com.aegean.icsd.mciwebapp.observations.beans;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;

public class ObservationResponse {
  private String id;
  private String player;
  private long maxCompletionTime;
  private int level;
  private Difficulty difficulty;
  private List<ObservationItem> items;
  private List<String> words;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPlayer() {
    return player;
  }

  public void setPlayer(String player) {
    this.player = player;
  }

  public long getMaxCompletionTime() {
    return maxCompletionTime;
  }

  public void setMaxCompletionTime(long maxCompletionTime) {
    this.maxCompletionTime = maxCompletionTime;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  public List<ObservationItem> getItems() {
    return items;
  }

  public void setItems(List<ObservationItem> items) {
    this.items = items;
  }

  public List<String> getWords() {
    return words;
  }

  public void setWords(List<String> words) {
    this.words = words;
  }
}
