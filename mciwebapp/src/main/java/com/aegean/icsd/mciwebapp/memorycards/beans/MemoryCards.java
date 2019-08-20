package com.aegean.icsd.mciwebapp.memorycards.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.generator.beans.BaseGame;

@Entity(MemoryCards.NAME)
public class MemoryCards extends BaseGame {
  public static final String NAME = "MemoryCards";

  @DataProperty("displayTime")
  private Long displayTime;

  @DataProperty("objectsPerCards")
  private Integer objectsPerCards;

  public Long getDisplayTime() {
    return displayTime;
  }

  public void setDisplayTime(Long displayTime) {
    this.displayTime = displayTime;
  }

  public Integer getObjectsPerCards() {
    return objectsPerCards;
  }

  public void setObjectsPerCards(Integer objectsPerCards) {
    this.objectsPerCards = objectsPerCards;
  }
}
