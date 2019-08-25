package com.aegean.icsd.engine.rules.interfaces;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RulesException;

public interface IRules {

  EntityRules getGameRules(String gameName, Difficulty difficulty) throws RulesException;

  EntityRules getEntityRules(String entityName)  throws RulesException;

  EntityRestriction getEntityRestriction(String entityName, String restrictionName) throws RulesException;

  EntityProperty getProperty(String entityName, String propertyName) throws RulesException;
}
