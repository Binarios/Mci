package com.aegean.icsd.mciwebapp.numberorder.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.numberorder.beans.NumberOrder;
import com.aegean.icsd.mciwebapp.numberorder.beans.NumberOrderResponse;
import com.aegean.icsd.mciwebapp.numberorder.interfaces.INumberOrderSvc;

public class NumberOrderSvc extends AbstractGameSvc<NumberOrder, NumberOrderResponse> implements INumberOrderSvc {

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Override
  protected void handleDataTypeRestrictions(String fullName, NumberOrder toCreate) throws MciException {
    EntityRestriction hasNumberValueRes;
    try {
      hasNumberValueRes = rules.getEntityRestriction(fullName, "hasNumberValue");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(NumberOrder.NAME, e);
    }

    List<Long> numbers = new ArrayList<>();
    Long number = generator.generateLongDataValue(hasNumberValueRes.getDataRange());

  }

  @Override
  protected void handleObjectRestrictions(String fullName, NumberOrder toCreate) throws MciException {
    // no object restrictions
  }

  @Override
  protected boolean isValid(Object solution) {
    return false;
  }

  @Override
  protected boolean checkSolution(NumberOrder game, Object solution) throws MciException {
    return false;
  }

  @Override
  protected NumberOrderResponse toResponse(NumberOrder toCreate) throws MciException {
    return null;
  }
}
