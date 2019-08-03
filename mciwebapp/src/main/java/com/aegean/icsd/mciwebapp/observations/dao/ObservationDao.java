package com.aegean.icsd.mciwebapp.observations.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;

@Repository
public class ObservationDao implements IObservationDao {

  private final static String gameName = "Observation";

  @Autowired
  private IGenerator generator;

  @Override
  public Observation generateCoreGameInstance(String playerName, Difficulty difficulty, int newLevel) throws ObservationsException {

    GameInfo info = new GameInfo();
    info.setGameName(gameName);
    info.setDifficulty(difficulty);
    info.setLevel("" + newLevel);
    info.setPlayerName(playerName);

    try {
      info = generator.instantiateGame(info);
    } catch (EngineException e) {
      throw Exceptions.GenerationError(e);
    }

    return toObservation(info);
  }


  @Override
  public String getLastCompletedLevel(Difficulty difficulty, String playerName) {
    GameInfo individualInfo = generator.getLastGeneratedIndividual(gameName, difficulty, playerName);
    String lastLevel = "0";
    if (individualInfo != null) {
      lastLevel = individualInfo.getLevel();
    }
    return lastLevel;
  }

  Observation toObservation(GameInfo info) {
    Observation obs = new Observation();
    obs.setId(info.getId());
    obs.setPlayerName(info.getPlayerName());
    obs.setLevel(info.getLevel());
    obs.setMaxCompletionTime(info.getMaxCompletionTime());
    obs.setDifficulty(info.getDifficulty());
    obs.setCompletedDate(info.getCompletedDate());
    obs.setCompletionTime(info.getCompletionTime());
    return obs;
  }

}
