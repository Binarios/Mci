package com.aegean.icsd.engine.rules.beans;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;

public class GameRules {
  /**
   * The game name
   */
  private String gameName;

  /**
   * The difficulty
   */
  private Difficulty difficulty;

  /**
   * List with all the restriction for the game
   */
  private List<GameRestriction> gameRestrictions;

  /**
   * List of all properties associated with the game
   */
  private List<GameProperty> properties;

  public String getGameName() {
    return gameName;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  public List<GameRestriction> getGameRestrictions() {
    return gameRestrictions;
  }

  public void setGameRestrictions(List<GameRestriction> gameRestrictions) {
    this.gameRestrictions = gameRestrictions;
  }

  public List<GameProperty> getProperties() {
    return properties;
  }

  public void setProperties(List<GameProperty> properties) {
    this.properties = properties;
  }
}
