package com.aegean.icsd.mciwebapp.observations.dao;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;

public interface IObservationDao {
  String getLastCompletedLevel(Difficulty difficulty, String playerName);

  String getAssociatedSubject(String observationObjId) throws ObservationsException;
}
