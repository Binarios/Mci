package com.aegean.icsd.engine.generator.implementations;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.generator.beans.GeneratorException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.GameProperty;
import com.aegean.icsd.engine.rules.beans.GameRestriction;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;

@Service
public class Generator implements IGenerator {

  @Autowired
  private IRules rules;

  @Override
  public GameInfo generateGame(String gameName, Difficulty difficulty, String playerName) throws GeneratorException {
    if(StringUtils.isEmpty(gameName)
      || StringUtils.isEmpty(playerName)
      || difficulty == null) {
      throw Exceptions.InvalidParameters();
    }

    String fullGameName = Utils.getFullGameName(gameName, difficulty);
    GameRules gameRules;
    try {
      gameRules = rules.getGameRules(gameName, difficulty);
    } catch (RulesException e) {
      throw  Exceptions.CannotRetrieveRules(fullGameName, e);
    }

    UUID individualId = generateGameIndividual(fullGameName, playerName);

    for (GameRestriction restriction : gameRules.getGameRestrictions()) {
      UUID restrictionId = generateRestriction(individualId, restriction);
    }

    for (GameProperty property : gameRules.getProperties()) {
      UUID propertyId = generateProperty(individualId, property);
    }

    GameInfo info = new GameInfo();
    info.setId(individualId);
    info.setPlayerName(playerName);
    info.setMaxCompletionTime("");
    info.setLevel("");

    return info;
  }

  UUID generateProperty(UUID individualId, GameProperty property) {
    return null;
  }

  UUID generateRestriction(UUID individualId, GameRestriction restriction) {
    return null;
  }

  UUID generateGameIndividual(String gameName, String playerName) {



    return null;
  }

}
