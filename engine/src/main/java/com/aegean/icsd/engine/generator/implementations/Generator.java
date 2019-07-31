package com.aegean.icsd.engine.generator.implementations;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.generator.dao.IGeneratorDao;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.interfaces.IRules;

@Service
public class Generator implements IGenerator {

  @Autowired
  private IRules rules;

  @Autowired
  private IGeneratorDao dao;


  @Override
  public GameInfo createCoreIndividual(GameInfo info) throws EngineException {
    if (info == null) {
      throw Exceptions.InvalidParameters();
    }
    String gameId = generateId(info.getPlayerName(), info.getGameName(), info.getDifficulty(), info.getLevel());
    info.setId(gameId);

    try {
      boolean success = dao.generateBasicGame(info);
      return success ? info : null ;
    } catch (EngineException e) {
      throw Exceptions.CannotCreateCoreGame(gameId, e);
    }
  }

  @Override
  public GameInfo getLastGeneratedIndividual(String gameName, Difficulty difficulty, String playerName) {
    return null;
  }

  String generateId ( String playerName, String gameName, Difficulty difficulty, String level) {
    String fullGameName = Utils.getFullGameName(gameName,difficulty);
    return StringUtils.capitalize(playerName) + StringUtils.capitalize(fullGameName) + level;
  }
}
