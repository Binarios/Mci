package com.aegean.icsd.engine.rules.interfaces;

import java.sql.Struct;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RulesException;

public interface IRules {

  GameRules getGameRules(String gameName, Difficulty difficulty) throws RulesException;

  EntityRules getEntityRules(String entity) throws RulesException;
}
