package com.aegean.icsd.mciwebapp.observations.dao;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;

public interface IObservationDao {

  int getLastCompletedLevel(Difficulty difficulty, String playerName) throws ObservationsException;

  List<String> getAssociatedSubjects(String id) throws ObservationsException;

  String getImagePath(String id) throws ObservationsException;
}
