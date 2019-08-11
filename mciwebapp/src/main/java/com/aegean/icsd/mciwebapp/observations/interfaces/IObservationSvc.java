package com.aegean.icsd.mciwebapp.observations.interfaces;

import java.util.List;
import java.util.Map;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;
public interface IObservationSvc {

  List<ObservationResponse> getObservations(String playerName) throws MciException;

  ObservationResponse createObservation(String playerName, Difficulty difficulty) throws MciException;

  ObservationResponse getObservation(String id, String player) throws MciException;

  ObservationResponse solveGame(String id, String player, Long completionTime,
                                Map<String, Integer> solution) throws MciException;
}
