package com.aegean.icsd.mciobjects.blocks.interfaces;

import java.util.List;

import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.mciobjects.blocks.beans.Block;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;

public interface IBlockProvider {
  List<Block> getNewBlockForEntity(String entityName, int nbRows, int nbCols) throws ProviderException;

  List<Block> selectBlocksByEntityId(String entityId) throws ProviderException;

  List<Block> selectBlocksByEntityIdOnProperty(String entityId, EntityProperty onProperty) throws ProviderException;
}
