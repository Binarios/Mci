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
    Difficulty res;
    if ("easy".equals(name.toLowerCase())) {
      res = Difficulty.EASY;
    } else if ("medium".equals(name.toLowerCase())) {
      res = Difficulty.MEDIUM;
    } else if ("hard".equals(name.toLowerCase())) {
      res = Difficulty.HARD;
    } else {
      throw new IllegalArgumentException("No difficulty defined with the name: " + name);
    }
    return res;
  }
}
