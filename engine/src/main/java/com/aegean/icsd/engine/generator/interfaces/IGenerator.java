package com.aegean.icsd.engine.generator.interfaces;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;

public interface IGenerator {

  String upsertObj(Object object) throws EngineException;

  GameInfo getLastGeneratedIndividual(String gameName, Difficulty difficulty, String playerName);

  boolean createObjRelation(String id, EntityProperty onProperty, String rangeId) throws EngineException;

  int generateIntDataValue(ValueRangeRestriction res);

}
