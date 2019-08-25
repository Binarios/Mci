package com.aegean.icsd.mciwebapp.logicalorder.beans;

import java.util.List;

public class BlockSetItem {

  private String id;

  private List<BlockItem> blocks;

  private Integer order;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<BlockItem> getBlocks() {
    return blocks;
  }

  public void setBlocks(List<BlockItem> blocks) {
    this.blocks = blocks;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }
}
