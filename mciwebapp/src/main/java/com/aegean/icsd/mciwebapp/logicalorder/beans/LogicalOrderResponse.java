package com.aegean.icsd.mciwebapp.logicalorder.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class LogicalOrderResponse extends ServiceResponse<LogicalOrder> {

  private List<BlockSetItem> sequence;

  private List<BlockSetItem> choices;

  public LogicalOrderResponse(LogicalOrder game) {
    super(game);
  }

  public List<BlockSetItem> getSequence() {
    return sequence;
  }

  public void setSequence(List<BlockSetItem> sequence) {
    this.sequence = sequence;
  }

  public List<BlockSetItem> getChoices() {
    return choices;
  }

  public void setChoices(List<BlockSetItem> choices) {
    this.choices = choices;
  }
}
