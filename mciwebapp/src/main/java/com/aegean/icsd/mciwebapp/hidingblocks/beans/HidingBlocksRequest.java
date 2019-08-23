package com.aegean.icsd.mciwebapp.hidingblocks.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.Request;

public class HidingBlocksRequest extends Request {
  private List<BlockItem> solution;

  public List<BlockItem> getSolution() {
    return solution;
  }

  public void setSolution(List<BlockItem> solution) {
    this.solution = solution;
  }
}
