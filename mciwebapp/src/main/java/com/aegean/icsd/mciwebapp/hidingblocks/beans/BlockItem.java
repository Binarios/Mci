package com.aegean.icsd.mciwebapp.hidingblocks.beans;

public class BlockItem {
  private String id;
  private Integer row;
  private Integer col;
  private Boolean hide;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getRow() {
    return row;
  }

  public void setRow(Integer row) {
    this.row = row;
  }

  public Integer getCol() {
    return col;
  }

  public void setCol(Integer col) {
    this.col = col;
  }

  public Boolean isHide() {
    return hide;
  }

  public void setHide(Boolean hide) {
    this.hide = hide;
  }
}
