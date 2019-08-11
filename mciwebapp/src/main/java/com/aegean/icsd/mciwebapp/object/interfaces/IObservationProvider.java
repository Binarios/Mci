package com.aegean.icsd.mciwebapp.object.interfaces;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IObservationProvider {
  String getObservationId(int totalImageNumber) throws ProviderException;
}
