package com.aegean.icsd.mciwebapp.observations.interfaces;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;
public interface IObservationSvc {
  ObservationResponse createObservation(String playerName, Difficulty difficulty) throws MciException;

}
