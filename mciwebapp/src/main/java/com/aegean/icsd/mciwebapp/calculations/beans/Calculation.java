package com.aegean.icsd.mciwebapp.calculations.beans;

import com.aegean.icsd.engine.common.beans.BaseGame;
import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;

@Entity(Calculation.NAME)
public class Calculation extends BaseGame {
  public static final String NAME = "Calculation";

  @DataProperty("hasSquareRows")
  private Integer totalRows;

  @DataProperty("hasSquareColumns")
  private Integer totalColumns;

  @DataProperty("calculationsPerInstance")
  private Integer calculationsPerInstance;

  @DataProperty("comparisonsPerColumn")
  private Integer comparisonsPerInstance;

  public Integer getTotalRows() {
    return totalRows;
  }

  public void setTotalRows(Integer totalRows) {
    this.totalRows = totalRows;
  }

  public Integer getTotalColumns() {
    return totalColumns;
  }

  public void setTotalColumns(Integer totalColumns) {
    this.totalColumns = totalColumns;
  }

  public Integer getCalculationsPerInstance() {
    return calculationsPerInstance;
  }

  public void setCalculationsPerInstance(Integer calculationsPerInstance) {
    this.calculationsPerInstance = calculationsPerInstance;
  }

  public Integer getComparisonsPerInstance() {
    return comparisonsPerInstance;
  }

  public void setComparisonsPerInstance(Integer comparisonsPerInstance) {
    this.comparisonsPerInstance = comparisonsPerInstance;
  }
}
