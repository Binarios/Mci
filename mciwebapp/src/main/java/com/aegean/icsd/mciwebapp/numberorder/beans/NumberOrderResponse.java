package com.aegean.icsd.mciwebapp.numberorder.beans;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class NumberOrderResponse extends ServiceResponse<NumberOrder> {
  public NumberOrderResponse(NumberOrder game) {
    super(game);
  }
}
