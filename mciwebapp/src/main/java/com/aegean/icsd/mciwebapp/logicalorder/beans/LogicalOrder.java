package com.aegean.icsd.mciwebapp.logicalorder.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.generator.beans.BaseGame;

@Entity(LogicalOrder.NAME)
public class LogicalOrder extends BaseGame {
  public static final String NAME = "LogicalOrder";

  @DataProperty("hasStep")
  private Integer step;

  @DataProperty("hasTotalColumns")
  private Integer columns;

  @DataProperty("hasTotalRows")
  private Integer rows;

  @DataProperty("hasTotalMovingBlocks")
  private Integer totalMovingBlocks;

  @DataProperty("hasMovement")
  private String movement;

  public Integer getStep() {
    return step;
  }

  public void setStep(Integer step) {
    this.step = step;
  }

  public Integer getColumns() {
    return columns;
  }

  public void setColumns(Integer columns) {
    this.columns = columns;
  }

  public Integer getRows() {
    return rows;
  }

  public void setRows(Integer rows) {
    this.rows = rows;
  }

  public Integer getTotalMovingBlocks() {
    return totalMovingBlocks;
  }

  public void setTotalMovingBlocks(Integer totalMovingBlocks) {
    this.totalMovingBlocks = totalMovingBlocks;
  }

  public String getMovement() {
    return movement;
  }

  public void setMovement(String movement) {
    this.movement = movement;
  }

}
