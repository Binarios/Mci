package com.aegean.icsd.mciwebapp.synonims.implementations;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.synonims.beans.SynonymResponse;
import com.aegean.icsd.mciwebapp.synonims.interfaces.ISynonymsSvc;

public class SynonymImp implements ISynonymsSvc {

  @Override
  public List<SynonymResponse> getGames(String playerName) throws MciException {
    return null;
  }

  @Override
  public SynonymResponse createGame(String playerName, Difficulty difficulty) throws MciException {
    return null;
  }

  @Override
  public SynonymResponse getGame(String id, String player) throws MciException {
    return null;
  }

  @Override
  public SynonymResponse solveGame(String id, String player, Long completionTime, String solution) throws MciException {
    return null;
  }
}
