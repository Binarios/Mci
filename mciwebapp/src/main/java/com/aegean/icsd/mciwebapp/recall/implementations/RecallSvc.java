package com.aegean.icsd.mciwebapp.recall.implementations;

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
import com.aegean.icsd.mciwebapp.object.beans.NumberObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
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
    try {
      EntityRestriction displayTimeRes = rules.getEntityRestriction(fullName, "displayTime");
      toCreate.setDisplayTime(generator.generateLongDataValue(displayTimeRes.getDataRange()));
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(Recall.NAME, e);
    }
  }

  @Override
  protected void handleRestrictions(String fullName, Recall toCreate) throws MciException {
    EntityRestriction hasRecallNumberRes;
    EntityRestriction hasNumberRes;
    try {
      hasNumberRes = rules.getEntityRestriction(fullName, "hasNumber");
      hasRecallNumberRes = rules.getEntityRestriction(fullName, "hasRecallNumber");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(Recall.NAME, e);
    }



  }

  @Override
  protected boolean isValid(Object solution) {
    return !StringUtils.isEmpty(solution.toString());
  }

  @Override
  protected boolean checkSolution(Recall game, Object solution) throws MciException {
    String recallNumberNode = dao.getRecallNumberNode(game.getId());
    try {
      NumberObj solutionNumber = numberProvider.selectNumberByValue(Long.parseLong(solution.toString()));
      NumberObj recallNodeNumber = numberProvider.selectNumberByNumberNode(recallNumberNode);
      return solutionNumber.getValue().equals(recallNodeNumber.getValue());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Recall.NAME, e);
    }
  }

  @Override
  protected RecallResponse toResponse(Recall toCreate) throws MciException {
    RecallResponse response = new RecallResponse(toCreate);

    return response;
  }


}
