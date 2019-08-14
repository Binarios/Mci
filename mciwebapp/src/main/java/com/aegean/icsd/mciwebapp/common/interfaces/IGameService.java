package com.aegean.icsd.mciwebapp.common.interfaces;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.generator.beans.BaseGame;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public interface IGameService<T extends ServiceResponse<? extends BaseGame>> {
  List<T> getGames(String playerName) throws MciException;

  T createGame(String playerName, Difficulty difficulty) throws MciException;

  T getGame(String id, String player) throws MciException;
}
