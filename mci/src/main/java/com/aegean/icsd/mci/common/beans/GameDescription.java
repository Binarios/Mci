package com.aegean.icsd.mci.common.beans;

import java.util.List;

import com.aegean.icsd.mci.ontology.beans.Difficulty;

public class GameDescription {

  private String nameOfGame;
  private String id;
  private Difficulty difficulty;
  private Double maxCompletionTime;
  private String level;
  private List<String> triplets;

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

  public Double getMaxCompletionTime() {
    return maxCompletionTime;
  }

  public void setMaxCompletionTime(Double maxCompletionTime) {
    this.maxCompletionTime = maxCompletionTime;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getNameOfGame() {
    return nameOfGame;
  }

  public void setNameOfGame(String nameOfGame) {
    this.nameOfGame = nameOfGame;
  }

  public List<String> getTriplets() {
    return triplets;
  }

  public void setTriplets(List<String> triplets) {
    this.triplets = triplets;
  }
}
