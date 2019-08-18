package com.aegean.icsd.mciwebapp.recall.implementations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.object.interfaces.INumberProvider;
import com.aegean.icsd.mciwebapp.recall.beans.Recall;
import com.aegean.icsd.mciwebapp.recall.beans.RecallResponse;
import com.aegean.icsd.mciwebapp.recall.dao.IRecallDao;
import com.aegean.icsd.mciwebapp.recall.interfaces.IRecallSvc;

@Service
public class RecallSvc extends AbstractGameSvc<Recall, RecallResponse> implements IRecallSvc {

  @Autowired
  private IRules rules;

  @Autowired
  private IGenerator generator;

  @Autowired
  private INumberProvider numberProvider;

  @Autowired
  private IRecallDao dao;

  @Override
  protected void handleDataTypeRestrictions(String fullName, Recall toCreate) throws MciException {
    EntityRestriction hasSimilarNumbersRes;
    EntityRestriction displayTimeRes;
    EntityRestriction hasRecallNumberRes;
    EntityRestriction hasNumberRes;
    try {
      displayTimeRes = rules.getEntityRestriction(fullName, "displayTime");
      hasSimilarNumbersRes = rules.getEntityRestriction(fullName, "hasSimilarNumbers");
      hasNumberRes = rules.getEntityRestriction(fullName, "hasNumberValue");
      hasRecallNumberRes = rules.getEntityRestriction(fullName, "hasRecallNumberValue");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(Recall.NAME, e);
    }
    Long recallNumber;
    boolean exists;
    int count = 0;
    do {
      recallNumber = generator.generateLongDataValue(hasRecallNumberRes.getDataRange());
      exists = dao.existsWithRecallNumber(recallNumber);
      count ++;
    } while (exists && count < 100);

    if (count == 100) {
      throw  GameExceptions.GenerationError(Recall.NAME, "Tried to many times");
    }

    toCreate.setDisplayTime(generator.generateLongDataValue(displayTimeRes.getDataRange()));
    toCreate.setSimilar(Boolean.valueOf(hasSimilarNumbersRes.getDataRange().getRanges().get(0).getValue()));
    toCreate.setRecallNumber(recallNumber);

    List<Long> numbers = new ArrayList<>();
    List<String> fragments = Arrays.asList(recallNumber.toString().split(""));
    while (numbers.size() < hasNumberRes.getCardinality()) {
      Long newNumber;
      if (toCreate.isSimilar()) {
        Collections.shuffle(fragments, new Random(System.currentTimeMillis()));
        String generatedNumber = String.join("", fragments);
        newNumber = Long.parseLong(generatedNumber);
      } else {
        newNumber = generator.generateLongDataValue(hasNumberRes.getDataRange());
      }
      if (!numbers.contains(newNumber) && newNumber.toString().length() == recallNumber.toString().length()) {
        numbers.add(newNumber);
      }
    }

    toCreate.setNumbers(numbers);
  }

  @Override
  protected void handleRestrictions(String fullName, Recall toCreate) throws MciException {
  }

  @Override
  protected boolean isValid(Object solution) {
    return !StringUtils.isEmpty(solution.toString());
  }

  @Override
  protected boolean checkSolution(Recall game, Object solution) throws MciException {
    Long recallNumber = dao.getRecallNumber(game.getId());
    Long solutionNumber = Long.parseLong(solution.toString());
    return solutionNumber.equals(recallNumber);
  }

  @Override
  protected RecallResponse toResponse(Recall toCreate) throws MciException {
    RecallResponse response = new RecallResponse(toCreate);
    return response;
  }


}
