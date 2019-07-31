package com.aegean.icsd.engine.generator.interfaces;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;

public interface IGenerator {

  GameInfo createCoreIndividual(GameInfo info) throws EngineException;

  GameInfo getLastGeneratedIndividual(String gameName, Difficulty difficulty, String playerName);
}
