package com.aegean.icsd.engine.common.beans;

public enum Difficulty {
  EASY("Easy"),
  MEDIUM("Medium"),
  HARD("Hard");

  private String normalizedName;
  private Difficulty(String normalizedName) {
    this.normalizedName = normalizedName;
  }

  public String getNormalizedName() {
    return this.normalizedName;
  }
}
