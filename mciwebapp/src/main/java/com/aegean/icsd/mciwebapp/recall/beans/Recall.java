package com.aegean.icsd.mciwebapp.recall.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.generator.beans.BaseGame;

@Entity(Recall.NAME)
public class Recall extends BaseGame {
  public static final String NAME = "Recall";

  @DataProperty("displayTime")
  private Long displayTime;

  public Long getDisplayTime() {
    return displayTime;
  }

  public void setDisplayTime(Long displayTime) {
    this.displayTime = displayTime;
  }
}
