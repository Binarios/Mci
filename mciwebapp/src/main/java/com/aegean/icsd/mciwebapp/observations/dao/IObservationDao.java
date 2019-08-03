package com.aegean.icsd.mciwebapp.observations.dao;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;

public interface IObservationDao {

  Observation generateCoreGameInstance(String playerName, Difficulty difficulty, int newLevel) throws ObservationsException;
  String getLastCompletedLevel(Difficulty difficulty, String playerName);
}
