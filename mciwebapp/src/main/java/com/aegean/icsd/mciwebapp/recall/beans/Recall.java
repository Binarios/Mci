package com.aegean.icsd.mciwebapp.recall.beans;

import java.util.List;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.generator.beans.BaseGame;

@Entity(Recall.NAME)
public class Recall extends BaseGame {
  public static final String NAME = "Recall";

  @DataProperty("displayTime")
  private Long displayTime;

  @DataProperty("hasSimilarNumbers")
  private Boolean similar;

  @DataProperty("hasRecallNumberValue")
  private Long recallNumber;

  @DataProperty("hasNumberValue")
  private List<Long> numbers;

  public Long getDisplayTime() {
    return displayTime;
  }

  public void setDisplayTime(Long displayTime) {
    this.displayTime = displayTime;
  }

  public Boolean isSimilar() {
    return similar;
  }

  public void setSimilar(Boolean similar) {
    this.similar = similar;
  }

  public Long getRecallNumber() {
    return recallNumber;
  }

  public void setRecallNumber(Long recallNumber) {
    this.recallNumber = recallNumber;
  }

  public List<Long> getNumbers() {
    return numbers;
  }

  public void setNumbers(List<Long> numbers) {
    this.numbers = numbers;
  }
}
