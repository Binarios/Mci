package com.aegean.icsd.mciwebapp.common.interfaces;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.generator.beans.BaseGame;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public interface IGameService<T extends BaseGame, R extends ServiceResponse<T>> {

  List<ServiceResponse<T>> getGames(String playerName, Class<T> gameClass) throws MciException;

  R createGame(String playerName, Difficulty difficulty, Class<T> gameClass) throws MciException;

  R getGame(String id, String player, Class<T> gameClass) throws MciException;

  R solveGame(String id, String player, Long completionTime, Object solution, Class<T> gameClass) throws MciException;
}
