package com.aegean.icsd.mci.observations;

import com.aegean.icsd.mci.common.beans.GameDescription;

public interface IObservationSvc {

  GameDescription createObservationGame(String difficulty, String level);
}
