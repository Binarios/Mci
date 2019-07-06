package com.aegean.icsd.engine.rules.interfaces;

import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RulesException;

public interface IRules {

  GameRules getGameRules(String gameName, String difficulty) throws RulesException;
}
