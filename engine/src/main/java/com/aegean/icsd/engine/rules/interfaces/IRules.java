package com.aegean.icsd.engine.rules.interfaces;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RulesException;

public interface IRules {

  GameRules getGameRules(String gameName, Difficulty difficulty) throws RulesException;
}
