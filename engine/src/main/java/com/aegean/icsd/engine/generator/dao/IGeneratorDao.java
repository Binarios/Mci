package com.aegean.icsd.engine.generator.dao;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;

public interface IGeneratorDao {

  int getLatestLevel(String gameName, String playerName) throws EngineException;

  boolean generateBasicGame(GameInfo info) throws EngineException;

  String getPrefixedName(String entity);

  String generateNodeName(String entity);
}
