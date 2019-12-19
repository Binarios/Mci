package com.aegean.icsd.engine.generator.interfaces;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.common.beans.BaseGame;
import com.aegean.icsd.engine.common.beans.BaseGameObject;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;

public interface IGenerator {

  <GAME_OBJECT extends BaseGameObject> List<GAME_OBJECT> selectGameObject(GAME_OBJECT criteria) throws EngineException;

  <GAME extends BaseGame> List<GAME> selectGame(GAME criteria) throws EngineException;

  <GAME extends BaseGame> String upsertGame(GAME game) throws EngineException;

  <GAME_OBJECT extends BaseGameObject> String upsertGameObject(GAME_OBJECT gameObject) throws EngineException;

  <GAME extends BaseGame, GAME_OBJECT extends BaseGameObject> boolean createObjRelation(GAME thisGame, GAME_OBJECT thatObj, EntityProperty onProperty) throws EngineException;

  <GAME_OBJECT extends BaseGameObject> boolean createObjRelation(GAME_OBJECT thisObj, GAME_OBJECT thatObj, EntityProperty onProperty) throws EngineException;

  int getLastCompletedLevel(String gameName, Difficulty difficulty, String playerName) throws EngineException;

  Long generateLongDataValue(ValueRangeRestriction res);
}
