package com.aegean.icsd.mciwebapp.logicalorder.implementations;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.logicalorder.beans.LogicalOrder;
import com.aegean.icsd.mciwebapp.logicalorder.beans.LogicalOrderResponse;
import com.aegean.icsd.mciwebapp.logicalorder.interfaces.ILogicalOrderSvc;

public class LogicalOrderSvc extends AbstractGameSvc<LogicalOrder, LogicalOrderResponse> implements ILogicalOrderSvc {

  @Override
  protected void handleDataTypeRestrictions(String fullName, LogicalOrder toCreate) throws MciException {

  }

  @Override
  protected void handleObjectRestrictions(String fullName, LogicalOrder toCreate) throws MciException {

  }

  @Override
  protected boolean isValid(Object solution) {
    return false;
  }

  @Override
  protected boolean checkSolution(LogicalOrder game, Object solution) throws MciException {
    return false;
  }

  @Override
  protected LogicalOrderResponse toResponse(LogicalOrder game) throws MciException {
    return null;
  }
}
