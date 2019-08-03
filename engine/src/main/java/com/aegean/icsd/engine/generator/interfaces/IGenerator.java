package com.aegean.icsd.engine.generator.interfaces;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.engine.rules.beans.EntityProperty;

public interface IGenerator {

  GameInfo instantiateGame(GameInfo info) throws EngineException;

  GameInfo getLastGeneratedIndividual(String gameName, Difficulty difficulty, String playerName);

  boolean createValueRelation(String id, EntityProperty onProperty, String rangeValue) throws EngineException;

  boolean createObjRelation(String id, EntityProperty onProperty, String rangeId) throws EngineException;

  List<String> instantiateObjects(String objectType, int cardinality) throws EngineException;

  String instantiateObject(String objectType) throws EngineException;
}
