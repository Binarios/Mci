package com.aegean.icsd.mciwebapp.observations.interfaces;

import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;

public interface IObservationSvc {
  Observation createObservation(Observation observation) throws ObservationsException;
}
