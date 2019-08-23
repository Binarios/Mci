package com.aegean.icsd.mciwebapp.hidingblocks.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class HidingBlocksResponse extends ServiceResponse<HidingBlocks> {

  private List<BlockItem> blocks;

  public HidingBlocksResponse(HidingBlocks game) {
    super(game);
  }

  public List<BlockItem> getBlocks() {
    return blocks;
  }

  public void setBlocks(List<BlockItem> blocks) {
    this.blocks = blocks;
  }

}
