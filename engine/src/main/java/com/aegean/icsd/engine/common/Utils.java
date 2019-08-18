package com.aegean.icsd.engine.common;

import com.aegean.icsd.engine.common.beans.Difficulty;

public class Utils {

  private Utils () { }

  public static String getFullGameName(String gameName, Difficulty difficulty) {
    return capitalize(difficulty.name()) + gameName;
  }

  public static String capitalize(String str) {
    String first = str.trim().substring(0, 1);
    String rest = str.trim().substring(1, str.length());
    String capitalFirst = first.toUpperCase();
    String lowerRest = rest.toLowerCase();
    return capitalFirst + lowerRest;
  }

}
