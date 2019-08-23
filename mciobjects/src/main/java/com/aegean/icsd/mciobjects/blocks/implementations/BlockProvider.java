package com.aegean.icsd.mciobjects.blocks.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
import com.aegean.icsd.mciobjects.blocks.interfaces.IBlockProvider;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.common.daos.IObjectsDao;
import com.aegean.icsd.mciobjects.common.implementations.ProviderExceptions;

@Service
public class BlockProvider implements IBlockProvider {

  @Autowired
  private IObjectsDao dao;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Override
  public List<Block> getBlocks(int nbRows, int nbCols) throws ProviderException {
    List<Block> blocks = new ArrayList<>();
    for (int row = 0; row < nbRows; row ++) {
      for (int col = 0; col < nbCols; col ++) {
        Block block = new Block();
        block.setRow(row);
        block.setColumn(col);
        try {
          List<Block> results = generator.selectGameObject(block);
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
    EntityRestriction hasBlock;
    EntityRestriction hasNextBlockSet;
    try {
      hasBlock = rules.getEntityRestriction(BlockSet.NAME, "hasBlock");
      hasNextBlockSet = rules.getEntityRestriction(BlockSet.NAME, "hasNextBlockSet");
    } catch (RulesException e) {
      throw ProviderExceptions.UnableToRetrieveRules(BlockSet.NAME, e);
    }

    List<BlockSet> blockSets = new ArrayList<>();
    for (int i = 0; i < nbBlockSet; i++) {
      BlockSet set = new BlockSet();
      List<Block> blocks = getBlocks(nbRows, nbCols);
      try {
        generator.upsertGameObject(set);
        for (Block block : blocks) {
          generator.createObjRelation(set.getId(), hasBlock.getOnProperty(), block.getId());
        }

        blockSets.add(set);
        if (i > 0) {
          generator.createObjRelation(blockSets.get(i - 1).getId(), hasNextBlockSet.getOnProperty(), set.getId());
        }
      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(BlockSet.NAME, e);
      }
    }
    return blockSets;
  }

  @Override
  public void updateMovingBlockFor(BlockSet blockSet, List<Block> toUpdate) throws ProviderException {
    EntityRestriction hasMovingBlock;
    try {
      hasMovingBlock = rules.getEntityRestriction(BlockSet.NAME, "hasMovingBlock");
    } catch (RulesException e) {
      throw ProviderExceptions.UnableToRetrieveRules(BlockSet.NAME, e);
    }

    for (Block block : toUpdate) {
      try {
        generator.createObjRelation(blockSet.getId(), hasMovingBlock.getOnProperty(), block.getId());
      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(BlockSet.NAME, e);
      }
    }

  }

  @Override
  public List<Block> selectBlocksByEntityId(String entityId) throws ProviderException {
    List<String> ids = dao.getAssociatedObjectsOfEntityId(entityId, Block.class);
    List<Block> blocks = new ArrayList<>();
    for (String id : ids) {
      Block block = new Block();
      block.setId(id);
      try {
        List<Block> results = generator.selectGameObject(block);
        blocks.add(results.get(0));
      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetObject(Block.NAME + " for entityId = " + entityId, e);
      }
    }
    return blocks;
  }

  @Override
  public List<Block> selectBlocksByEntityIdOnProperty(String entityId, EntityProperty onProperty)
    throws ProviderException {
    List<String> ids = dao.getAssociatedIdsOnPropertyForEntityId(entityId, onProperty, Block.class);
    List<Block> blocks = new ArrayList<>();
    for (String id : ids) {
      Block block = new Block();
      block.setId(id);
      try {
        List<Block> results = generator.selectGameObject(block);
        blocks.add(results.get(0));
      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetWord("entityId = " + entityId, e);
      }
    }
    return blocks;
  }


}
