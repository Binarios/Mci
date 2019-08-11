package com.aegean.icsd.mciwebapp.observations.dao;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationItem;

public interface IObservationDao {

  int getLastCompletedLevel(Difficulty difficulty, String playerName) throws MciException;

  List<String> getAssociatedSubjects(String id) throws MciException;

  String getImagePath(String id) throws MciException;

  List<Observation> getGamesForPlayer(String playerName) throws MciException;

  Observation getById(String id, String player) throws MciException;

  List<ObservationItem> getObservationItems(String id) throws MciException;

  boolean solveGame(String id, String player, String key, Integer value) throws MciException;
}
