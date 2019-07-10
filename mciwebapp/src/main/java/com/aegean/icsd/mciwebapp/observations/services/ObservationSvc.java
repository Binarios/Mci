package com.aegean.icsd.mciwebapp.observations.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.mciwebapp.observations.beans.Exceptions;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;
import com.aegean.icsd.mciwebapp.observations.interfaces.IObservationSvc;

@Service
public class ObservationSvc implements IObservationSvc {

  private final String gameName = "Observation";

  @Autowired
  private IGenerator generator;

  @Override
  public Observation createObservation(Observation observation) throws ObservationsException {
    if (observation == null
      || observation.getDifficulty() == null
      || StringUtils.isEmpty(observation.getPlayerName())) {
      throw Exceptions.InvalidRequest();
    }

    GameInfo info = generator.generateGame(gameName, observation.getDifficulty(), observation.getPlayerName());

    return null;
  }
}
