package com.aegean.icsd.mciobjects.blocks.implementations;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.blocks.beans.Block;
import com.aegean.icsd.mciobjects.blocks.beans.BlockSet;
import com.aegean.icsd.mciobjects.blocks.beans.NumberBlock;
import com.aegean.icsd.mciobjects.blocks.interfaces.IBlockProvider;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.common.daos.IObjectsDao;
import com.aegean.icsd.mciobjects.common.implementations.ProviderExceptions;

@Service
public class BlockProvider implements IBlockProvider {

  @Autowired
  private IRules rules;

  @Autowired
  private IObjectsDao dao;

  @Autowired
  private IGenerator generator;

  @Autowired
  private Map<String, EntityRestriction> blockRules;

  @Override
  public List<Block> getBlocks(int nbRows, int nbCols) throws ProviderException {
    return getBlocks(nbRows, nbCols, Block.class);
  }

  @Override
  public <T extends Block> List<T> getBlocks(int nbRows, int nbCols, Class<T> blockType) throws ProviderException {
    List<T> blocks = new ArrayList<>();
    for (int row = 0; row < nbRows; row ++) {
      for (int col = 0; col < nbCols; col ++) {
        T block;
        try {
          block = blockType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          throw ProviderExceptions.GenerationError(Block.NAME, e);
        }

        block.setRow(row);
        block.setColumn(col);
        try {
          List<T> results = generator.selectGameObject(block);
          if (results.isEmpty()) {
            generator.upsertGameObject(block);
          } else {
            block = results.get(0);
          }
          blocks.add(block);
        } catch (EngineException e) {
          throw ProviderExceptions.GenerationError(Block.NAME, e);
        }
      }
    }

    if (blocks.isEmpty()) {
      throw ProviderExceptions.UnableToGetObject(Block.NAME);
    }

    return blocks;
  }

  @Override
  public List<BlockSet> getNewBlockSets(int nbRows, int nbCols, int nbBlockSet) throws ProviderException {
    EntityRestriction hasBlock = blockRules.get("hasBlock");

    List<BlockSet> blockSets = new ArrayList<>();
    for (int i = 0; i < nbBlockSet; i++) {
      BlockSet set = new BlockSet();
      List<Block> blocks = getBlocks(nbRows, nbCols);
      try {
        generator.upsertGameObject(set);
        for (Block block : blocks) {
          generator.createObjRelation(set, block, hasBlock.getOnProperty());
        }
        set.setBlocks(blocks);
        blockSets.add(set);
      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(BlockSet.NAME, e);
      }
    }
    return blockSets;
  }

  @Override
  public void orderBlockSets(List<BlockSet> blockSets) throws ProviderException {
    EntityRestriction hasPreviousBlockSet = blockRules.get("hasPreviousBlockSet");
    try {
      blockSets.sort(Comparator.comparingInt(BlockSet::getOrder));
      generator.upsertGameObject(blockSets.get(0));
      for (int i = 1; i < blockSets.size(); i++) {
        generator.createObjRelation(blockSets.get(i), blockSets.get(i - 1), hasPreviousBlockSet.getOnProperty());
        generator.upsertGameObject(blockSets.get(i));
      }
    } catch (EngineException e) {
      throw ProviderExceptions.GenerationError(BlockSet.NAME, e);
    }
  }

  @Override
  public void updateMovingBlockFor(BlockSet blockSet, List<Block> toUpdate) throws ProviderException {
    EntityRestriction hasMovingBlock = blockRules.get("hasMovingBlock");

    for (Block block : toUpdate) {
      try {
        generator.createObjRelation(blockSet, block, hasMovingBlock.getOnProperty());
      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(BlockSet.NAME, e);
      }
    }
  }

  @Override
  public <T extends Block> void connect(T thisBlock, T thatBlock) throws ProviderException {
    EntityRestriction hasConnectingBlock;
    try {
      hasConnectingBlock = rules.getEntityRestriction(thisBlock.getClass(), "hasConnectingBlock");
      generator.createObjRelation(thisBlock, thatBlock, hasConnectingBlock.getOnProperty());
    } catch (EngineException | RulesException e) {
      throw ProviderExceptions.GenerationError(Block.NAME, e);
    }
  }

  @Override
  public <BLOCK extends Block> void createBlock(BLOCK block) throws ProviderException {
    try {
      generator.upsertGameObject(block);
    } catch (EngineException e) {
      throw ProviderExceptions.GenerationError(Block.NAME, e);
    }
  }

  @Override
  public List<Block> selectBlocksByEntityId(String entityId) throws ProviderException {
    List<String> ids = dao.getAssociatedObjectsOfEntityId(entityId, Block.class);
    return getBlocksFromIds(ids);
  }

  @Override
  public List<Block> selectBlocksByEntityIdOnProperty(String entityId, EntityProperty onProperty)
    throws ProviderException {
    List<String> ids = dao.getAssociatedIdsOnPropertyForEntityId(entityId, onProperty, Block.class);
    return getBlocksFromIds(ids);
  }

  @Override
  public List<BlockSet> selectBlockSetsByEntityIdOnProperty(String entityId, EntityProperty onProperty)
    throws ProviderException {
    List<String> ids = dao.getAssociatedIdsOnPropertyForEntityId(entityId, onProperty, BlockSet.class);
    return getBlockSetsFromIds(ids);
  }

  List<Block> getBlocksFromIds(List<String> ids) throws ProviderException {
    List<Block> blocks = new ArrayList<>();
    for (String id : ids) {
      Block block = new Block();
      block.setId(id);
      try {
        List<Block> results = generator.selectGameObject(block);
        blocks.add(results.get(0));
      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetObject("id = " + id, e);
      }
    }
    return blocks;
  }

  List<BlockSet> getBlockSetsFromIds(List<String> ids) throws ProviderException {
    EntityRestriction hasMovingBlock = blockRules.get("hasMovingBlock");

    List<BlockSet> blockSets = new ArrayList<>();
    for (String id : ids) {
      BlockSet blockSet = new BlockSet();
      blockSet.setId(id);
      try {
        BlockSet result = generator.selectGameObject(blockSet).get(0);
        List<Block> blocks = selectBlocksByEntityId(result.getId());
        List<Block> movingBlocks = selectBlocksByEntityIdOnProperty(result.getId(), hasMovingBlock.getOnProperty());
        result.setBlocks(blocks);
        result.setMovingBlocks(movingBlocks);
        blockSets.add(result);
      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetObject("id = " + id, e);
      }
    }
    return blockSets;
  }

}
