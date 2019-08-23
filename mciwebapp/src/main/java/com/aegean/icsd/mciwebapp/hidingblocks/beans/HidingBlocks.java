package com.aegean.icsd.mciwebapp.hidingblocks.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.generator.beans.BaseGame;

@Entity(HidingBlocks.NAME)
public class HidingBlocks extends BaseGame {
  public static final String NAME = "HidingBlocks";

  @DataProperty("hasTotalRows")
  private Integer rows;

  @DataProperty("hasTotalColumns")
  private Integer columns;


  public Integer getRows() {
    return rows;
  }

  public void setRows(Integer rows) {
    this.rows = rows;
  }

  public Integer getColumns() {
    return columns;
  }

  public void setColumns(Integer columns) {
    this.columns = columns;
  }
}
