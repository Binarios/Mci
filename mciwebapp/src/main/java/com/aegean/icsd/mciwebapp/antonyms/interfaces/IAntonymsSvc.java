package com.aegean.icsd.mciwebapp.antonyms.interfaces;

import com.aegean.icsd.mciwebapp.antonyms.beans.AntonymResponse;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.interfaces.IGameService;
import com.aegean.icsd.mciwebapp.synonyms.beans.SynonymResponse;

public interface IAntonymsSvc extends IGameService<AntonymResponse> {

  AntonymResponse solveGame(String id, String player, Long completionTime, String solution) throws MciException;
}
