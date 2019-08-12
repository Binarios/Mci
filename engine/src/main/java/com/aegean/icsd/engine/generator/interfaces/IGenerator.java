package com.aegean.icsd.engine.generator.interfaces;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.BaseGame;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;

public interface IGenerator {

  String upsertObj(Object object) throws EngineException;

  String selectObjectId(Object object) throws EngineException;

  boolean createObjRelation(String id, EntityProperty onProperty, String rangeId) throws EngineException;

  int getLastCompletedLevel(String gameName, Difficulty difficulty, String playerName) throws EngineException;

  <T extends BaseGame> List<T> getGamesForPlayer(String gameName, String playerName, Class<T> gameObjClass)
      throws EngineException;

  int generateIntDataValue(ValueRangeRestriction res);

}
