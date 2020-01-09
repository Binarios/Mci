package com.aegean.icsd.mciwebapp.puzzles.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.common.beans.BaseGame;

@Entity(Puzzle.NAME)
public class Puzzle extends BaseGame {
  public static final String NAME = "Puzzle";

  @DataProperty("hasColumns")
  private Integer columns;

  @DataProperty("hasRows")
  private Integer rows;

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
}
