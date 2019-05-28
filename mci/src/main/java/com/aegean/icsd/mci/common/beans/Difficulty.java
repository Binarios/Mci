package com.aegean.icsd.mci.common.beans;

public enum Difficulty {

  EASY("easy"),
  MEDIUM("medium"),
  HARD("hard");


  private String name;

  Difficulty(String name) {
    this.name= name;
  }

  public String getName() { return this.name; }

  public static Difficulty fromName(String name) {
    return switch (name) {
      case "easy", "EASY" -> Difficulty.EASY;
      case "medium","MEDIUM" -> Difficulty.MEDIUM;
      case "hard","HARD" -> Difficulty.HARD;
      default -> throw new IllegalArgumentException("No difficulty defined with the name: " + name);
    };
  }
}
