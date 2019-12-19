package com.aegean.icsd.mciwebapp.calculations.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;
import com.aegean.icsd.mciwebapp.hidingblocks.beans.BlockItem;

public class CalculationResponse extends ServiceResponse<Calculation> {
  private List<BlockItem> blocks;

  public CalculationResponse(Calculation game) {
    super(game);
  }
}
