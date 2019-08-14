package com.aegean.icsd.mciwebapp.object.interfaces;

import com.aegean.icsd.mciwebapp.object.beans.ObservationObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IObservationProvider {
  ObservationObj getObservation(int totalImageNumber) throws ProviderException;
}
