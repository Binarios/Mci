package com.aegean.icsd.mciwebapp.numberorder.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.numberorder.beans.NumberOrder;
import com.aegean.icsd.mciwebapp.numberorder.beans.NumberOrderResponse;
import com.aegean.icsd.mciwebapp.numberorder.beans.SolutionItem;
import com.aegean.icsd.mciwebapp.numberorder.interfaces.INumberOrderSvc;

@Service
public class NumberOrderSvc extends AbstractGameSvc<NumberOrder, NumberOrderResponse> implements INumberOrderSvc {

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Override
  protected void handleDataTypeRestrictions(String fullName, NumberOrder toCreate) throws MciException {
    EntityRestriction hasNumberValueRes;
    EntityRestriction hasDescOrderRes;
    try {
      hasNumberValueRes = rules.getEntityRestriction(fullName, "hasNumberValue");
      hasDescOrderRes = rules.getEntityRestriction(fullName, "hasDescOrder");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(NumberOrder.NAME, e);
    }

    toCreate.setDescOrder(Boolean.valueOf(hasDescOrderRes.getDataRange().getRanges().get(0).getValue()));

    boolean repeat = true;
    int count = 0;
    while (repeat && count < 100) {
      List<Long> numbers = new ArrayList<>();
      for (int i = 0; i < hasNumberValueRes.getCardinality(); i++) {
        numbers.add(generator.generateLongDataValue(hasNumberValueRes.getDataRange()));
      }
      toCreate.setNumbers(numbers);
      try {
        List<NumberOrder> results = generator.selectGame(toCreate);
        if (results.isEmpty()) {
          repeat = false;
        }
        count ++;
      } catch (EngineException e) {
        throw GameExceptions.GenerationError(NumberOrder.NAME, e);
      }
    }
    if (count == 100) {
      throw GameExceptions.GenerationError(NumberOrder.NAME, "Cannot generate new game for this difficulty");
    }
  }

  @Override
  protected void handleObjectRestrictions(String fullName, NumberOrder toCreate) throws MciException {
    // no object restrictions
  }

  @Override
  protected boolean isValid(Object solution) {
    List<SolutionItem> solutionItems = (List<SolutionItem>) solution;
    return solutionItems != null && !solutionItems.isEmpty();
  }

  @Override
  protected boolean checkSolution(NumberOrder game, Object solution) throws MciException {
    List<Long> existingNumbers = game.getNumbers();
    List<SolutionItem> solutionItems = (List<SolutionItem>) solution;
    List<Long> solutionNumbers = solutionItems.stream().map(SolutionItem::getNumber).collect(Collectors.toList());

    if (existingNumbers.size() != solutionNumbers.size()) {
      throw GameExceptions.UnableToSolve(NumberOrder.NAME, "The provided number of numbers is incorrect");
    }

    List<Long> notFound = solutionNumbers.stream()
      .filter(solutionItem -> !existingNumbers.contains(solutionItem))
      .collect(Collectors.toList());

    if (!notFound.isEmpty()) {
      throw GameExceptions.UnableToSolve(NumberOrder.NAME, "The provided numbers are not part of the game");
    }

    Map<Integer, List<SolutionItem>> groupedByOrder = solutionItems.stream()
      .collect(Collectors.groupingBy(SolutionItem::getOrder));

    for (Map.Entry<Integer, List<SolutionItem>> entry : groupedByOrder.entrySet()) {
      if (entry.getValue().size() > 1) {
        throw GameExceptions.UnableToSolve(NumberOrder.NAME, "There are more than one item with order " + entry.getKey());
      }
    }

    Comparator comparator = Comparator.comparingInt(SolutionItem::getOrder);
    if (game.isDescOrder()) {
      comparator = comparator.reversed();
    }
    solutionItems.sort(comparator);

    boolean isSolved = true;
    Long temp = game.isDescOrder() ? Long.MAX_VALUE : Long.MIN_VALUE;
    for (SolutionItem item : solutionItems) {
      if (game.isDescOrder()) {
        isSolved &= temp > item.getNumber();
      } else {
        isSolved &= temp < item.getNumber();
      }
      temp = item.getNumber();
    }

    return isSolved;
  }

  @Override
  protected NumberOrderResponse toResponse(NumberOrder toCreate) throws MciException {
    Collections.shuffle(toCreate.getNumbers(), new Random(System.currentTimeMillis()));
    return new NumberOrderResponse(toCreate);
  }
}
