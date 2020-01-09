package com.aegean.icsd.mciobjects.blocks.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Key;
import com.aegean.icsd.engine.common.beans.BaseGameObject;

@Entity(Block.NAME)
public class Block extends BaseGameObject {
  public static final String NAME = "Block";

  @Key
  @DataProperty("hasRowNumber")
  private Integer row;

  @Key
  @DataProperty("hasColumnNumber")
  private Integer column;

  public void setRow(Integer row) {
    this.row = row;
  }

  public Integer getRow() {
    return this.row;
  }

  public void setColumn(Integer column) {
    this.column = column;
  }

  public Integer getColumn() {
    return this.column;
  }

}
