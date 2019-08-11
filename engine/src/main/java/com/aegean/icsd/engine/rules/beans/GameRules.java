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
  private List<EntityRestriction> restrictions;

  /**
   * List of all properties associated with the game
   */
  private List<EntityProperty> properties;

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

  public List<EntityRestriction> getRestrictions() {
    return restrictions;
  }

  public void setRestrictions(List<EntityRestriction> restrictions) {
    this.restrictions = restrictions;
  }

  public List<EntityProperty> getProperties() {
    return properties;
  }

  public void setProperties(List<EntityProperty> properties) {
    this.properties = properties;
  }
}
