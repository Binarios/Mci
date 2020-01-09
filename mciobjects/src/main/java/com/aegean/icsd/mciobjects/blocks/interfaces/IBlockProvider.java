package com.aegean.icsd.mciobjects.blocks.interfaces;

import java.util.List;

import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.mciobjects.blocks.beans.Block;
import com.aegean.icsd.mciobjects.blocks.beans.BlockSet;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;

public interface IBlockProvider {
  List<Block> getBlocks(int nbRows, int nbCols) throws ProviderException;

  <BLOCK extends Block> List<BLOCK> getBlocks(int nbRows, int nbCols, Class<BLOCK> blockType) throws ProviderException;

  List<BlockSet> getNewBlockSets(int nbRows, int nbCols, int nbBlockSet) throws ProviderException;

  void orderBlockSets(List<BlockSet> blockSets) throws ProviderException;

  void updateMovingBlockFor(BlockSet blockSet, List<Block> toUpdate) throws ProviderException;

  <BLOCK extends Block> void createBlock(BLOCK block) throws ProviderException;

  <BLOCK extends Block> void connect(BLOCK thisBlock, BLOCK thatBlock) throws ProviderException;

  List<Block> selectBlocksByEntityId(String entityId) throws ProviderException;

  List<Block> selectBlocksByEntityIdOnProperty(String entityId, EntityProperty onProperty) throws ProviderException;

  List<BlockSet> selectBlockSetsByEntityIdOnProperty(String entityId, EntityProperty onProperty) throws ProviderException;
}
