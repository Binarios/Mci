package com.aegean.icsd.engine.generator.interfaces;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.generator.beans.GeneratorException;

public interface IGenerator {

  GameInfo generateGame(String gameName, Difficulty difficulty, String playerName) throws GeneratorException;
}
