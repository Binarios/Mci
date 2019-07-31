package com.aegean.icsd.mciwebapp.observations.implementations;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;
import com.aegean.icsd.mciwebapp.observations.dao.IObservationDao;
import com.aegean.icsd.mciwebapp.observations.interfaces.IObservationSvc;

@Service
public class ObservationImpl implements IObservationSvc {

  private final String gameName = "Observation";

  @Autowired
  private IObservationDao dao;

  @Autowired
  private IRules rules;

  @Autowired
  private IGenerator generator;

  @Override
  public Observation createObservation(String playerName, Difficulty difficulty)
    throws ObservationsException {

    if (difficulty == null
      || StringUtils.isEmpty(playerName)) {
      throw Exceptions.InvalidRequest();
    }

    EntityRestriction maxTimeRes = null;
    try {
      maxTimeRes = rules.getEntityRestriction(gameName, difficulty, "maxCompletionTime");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveMaxCompletionTime(difficulty, e);
    }

    String maxTime = maxTimeRes.getDataRange().getRanges().get(0).getValue();
    String lastCompletedLevel = dao.getLastCompletedLevel(difficulty, playerName);
    int newLevel = Integer.parseInt(lastCompletedLevel) + 1;
    Observation observation = dao.generateCoreGameInstance(playerName, difficulty, newLevel, maxTime);



    return observation;
  }
}
