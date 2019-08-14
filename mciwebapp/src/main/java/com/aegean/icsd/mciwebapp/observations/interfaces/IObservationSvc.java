package com.aegean.icsd.mciwebapp.observations.interfaces;

import java.util.Map;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.interfaces.IGameService;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;

public interface IObservationSvc extends IGameService<ObservationResponse> {

  ObservationResponse solveGame(String id, String player, Long completionTime,
                                Map<String, Integer> solution) throws MciException;
}
