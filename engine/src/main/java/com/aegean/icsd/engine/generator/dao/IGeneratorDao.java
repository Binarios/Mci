package com.aegean.icsd.engine.generator.dao;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.queries.beans.InsertParam;

public interface IGeneratorDao {
  int getLatestLevel(String gameName, String playerName) throws EngineException;

  String generateBasicGame(String gameName) throws EngineException;

  String getPrefixedName(String entity);

  InsertParam constructInsParam(String varName, String varValue, boolean isIri);

  String generateNodeName(String entity);
}
