package com.aegean.icsd.mciwebapp.observations.dao;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;

public interface IObservationDao {

  int getLastCompletedLevel(Difficulty difficulty, String playerName) throws MciException;

  List<String> getAssociatedSubjects(String id) throws MciException;

  String getImagePath(String id) throws MciException;

  List<Observation> getGamesForPlayer(String playerName) throws MciException;
}
