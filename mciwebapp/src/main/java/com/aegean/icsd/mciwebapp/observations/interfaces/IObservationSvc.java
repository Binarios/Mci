package com.aegean.icsd.mciwebapp.observations.interfaces;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;

public interface IObservationSvc {
  Observation createObservation(String playerName, Difficulty difficulty) throws ObservationsException;
}
