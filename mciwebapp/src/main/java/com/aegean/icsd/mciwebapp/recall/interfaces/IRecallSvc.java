package com.aegean.icsd.mciwebapp.recall.interfaces;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.interfaces.IGameService;
import com.aegean.icsd.mciwebapp.recall.beans.RecallResponse;

public interface IRecallSvc extends IGameService<RecallResponse> {
  RecallResponse solveGame(String id, String player, Long completionTime,
                           Long solution) throws MciException;
}
