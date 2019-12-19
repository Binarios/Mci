package com.aegean.icsd.mciwebapp.numberorder.beans;

import java.util.List;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.common.beans.BaseGame;

@Entity(NumberOrder.NAME)
public class NumberOrder extends BaseGame {
  public static final String NAME = "NumberOrder";

  @DataProperty("hasNumberValue")
  private List<Long> numbers;

  @DataProperty("hasDescOrder")
  private Boolean descOrder;

  public List<Long> getNumbers() {
    return numbers;
  }

  public void setNumbers(List<Long> numbers) {
    this.numbers = numbers;
  }

  public void setDescOrder(Boolean descOrder) {
    this.descOrder = descOrder;
  }

  public Boolean isDescOrder() {
    return this.descOrder;
  }
}
