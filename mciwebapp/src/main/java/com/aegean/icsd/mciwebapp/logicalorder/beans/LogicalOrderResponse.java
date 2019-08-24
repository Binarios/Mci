package com.aegean.icsd.mciwebapp.logicalorder.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class LogicalOrderResponse extends ServiceResponse<LogicalOrder> {

  private List<BlockSetItem> sets;

  public LogicalOrderResponse(LogicalOrder game) {
    super(game);
  }

  public List<BlockSetItem> getSets() {
    return sets;
  }

  public void setSets(List<BlockSetItem> sets) {
    this.sets = sets;
  }
}
