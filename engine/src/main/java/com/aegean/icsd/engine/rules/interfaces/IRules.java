package com.aegean.icsd.engine.rules.interfaces;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RulesException;

public interface IRules {

  GameRules getGameRules(String gameName, Difficulty difficulty) throws RulesException;

  EntityRestriction getEntityRestriction(String gameName, Difficulty difficulty, String restrictionName) throws RulesException;
}
