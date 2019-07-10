package com.aegean.icsd.engine.common;

import com.aegean.icsd.engine.common.beans.Difficulty;

public class Utils {

  public static String getFullGameName(String gameName, Difficulty difficulty) {
    return  difficulty.getNormalizedName() + gameName;
  }
}
