package com.aegean.icsd.mciwebapp.observations.interfaces;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;
public interface IObservationSvc {

  List<ObservationResponse> getObservations(String playerName) throws MciException;

  ObservationResponse createObservation(String playerName, Difficulty difficulty) throws MciException;

  ObservationResponse getObservation(String id, String player) throws MciException;
}
