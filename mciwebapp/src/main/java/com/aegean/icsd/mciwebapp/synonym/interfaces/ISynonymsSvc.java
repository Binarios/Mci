package com.aegean.icsd.mciwebapp.synonym.interfaces;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.interfaces.IGameService;
import com.aegean.icsd.mciwebapp.synonym.beans.SynonymResponse;

public interface ISynonymsSvc extends IGameService<SynonymResponse> {

  SynonymResponse solveGame(String id, String player, Long completionTime, String solution) throws MciException;
}