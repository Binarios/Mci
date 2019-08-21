package com.aegean.icsd.mciobjects.observationobjs.interfaces;

import java.util.List;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.observationobjs.beans.ObservationObj;

public interface IObservationProvider {
  ObservationObj getObservation(int totalImageNumber) throws ProviderException;

  List<ObservationObj> selectObservationObjByEntityId(String entityId) throws ProviderException;
}
