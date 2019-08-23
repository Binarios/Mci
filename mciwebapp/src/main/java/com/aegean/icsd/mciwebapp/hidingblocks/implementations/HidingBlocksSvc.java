package com.aegean.icsd.mciwebapp.hidingblocks.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.blocks.beans.Block;
import com.aegean.icsd.mciobjects.blocks.interfaces.IBlockProvider;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.hidingblocks.beans.BlockItem;
import com.aegean.icsd.mciwebapp.hidingblocks.beans.HidingBlocks;
import com.aegean.icsd.mciwebapp.hidingblocks.beans.HidingBlocksResponse;
import com.aegean.icsd.mciwebapp.hidingblocks.interfaces.IHidingBlocksSvc;

@Service
public class HidingBlocksSvc extends AbstractGameSvc<HidingBlocks, HidingBlocksResponse> implements IHidingBlocksSvc {

  @Autowired
  private IRules rules;

  @Autowired
  private IBlockProvider blockProvider;


  @Override
  protected void handleDataTypeRestrictions(String fullName, HidingBlocks toCreate) throws MciException {
    EntityRestriction hasTotalRows;
    EntityRestriction hasTotalColumns;

    try {
      hasTotalRows = rules.getEntityRestriction(fullName, "hasTotalRows");
      hasTotalColumns = rules.getEntityRestriction(fullName, "hasTotalColumns");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(HidingBlocks.NAME, e);
    }

    Integer height = Integer.parseInt(hasTotalRows.getDataRange().getRanges().get(0).getValue());
    Integer width = Integer.parseInt(hasTotalColumns.getDataRange().getRanges().get(0).getValue());

    toCreate.setRows(height);
    toCreate.setColumns(width);

  }

  @Override
  protected void handleObjectRestrictions(String fullName, HidingBlocks toCreate) throws MciException {
    EntityRestriction hasBlock;
    EntityRestriction hasHidingBlock;

    try {
      hasBlock = rules.getEntityRestriction(fullName, "hasBlock");
      hasHidingBlock = rules.getEntityRestriction(fullName, "hasHidingBlock");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(HidingBlocks.NAME, e);
    }

    List<Block> blocks;
    try {
      blocks = blockProvider.getNewBlockForEntity(fullName, toCreate.getColumns(), toCreate.getRows());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(HidingBlocks.NAME, e);
    }

    if (blocks.size() != hasBlock.getCardinality()) {
      throw GameExceptions.GenerationError(HidingBlocks.NAME, String.format("The total amount of provided blocks (%s)" +
        " doesn't match the required amount (%s)" , blocks.size(), hasBlock.getCardinality()));
    }

    List<Block> forHiding = new ArrayList<>();
    Collections.shuffle(blocks, new Random(System.currentTimeMillis()));
    for (int i = 0; i < hasHidingBlock.getCardinality(); i++) {
      Block toHide = blocks.get(ThreadLocalRandom.current().nextInt(0, blocks.size()));
      forHiding.add(toHide);
    }

    createObjRelation(toCreate, blocks, hasBlock.getOnProperty());
    createObjRelation(toCreate, forHiding, hasHidingBlock.getOnProperty());
  }

  @Override
  protected boolean isValid(Object solution) {
    List<String> castedSolution = (List) solution;
    return castedSolution != null && !castedSolution.isEmpty();
  }

  @Override
  protected boolean checkSolution(HidingBlocks game, Object solution) throws MciException {
    String fullName = getFullGameName(game);

    EntityRestriction hasHidingBlock;
    try {
      hasHidingBlock = rules.getEntityRestriction(fullName, "hasHidingBlock");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(HidingBlocks.NAME, e);
    }

    List<BlockItem> castedSolution = (List) solution;
    List<Block> existingBlocks;
    try {
      existingBlocks = blockProvider.selectBlocksByEntityIdOnProperty(game.getId(), hasHidingBlock.getOnProperty());
    } catch (ProviderException e) {
      throw GameExceptions.UnableToSolve(HidingBlocks.NAME, e);
    }

    if (existingBlocks.size() != castedSolution.size()) {
      throw GameExceptions.UnableToSolve(HidingBlocks.NAME, "Provided solution has wrong number of items");
    }

    List<Block> found = existingBlocks.stream()
      .filter(existing -> {
        List<BlockItem> foundSolutionItems = castedSolution.stream()
          .filter(BlockItem::isHide)
          .filter(solutionItem -> solutionItem.getId().equals(existing.getId()))
          .collect(Collectors.toList());
        return foundSolutionItems.size() == 1;
      })
      .collect(Collectors.toList());

    return found.size() == hasHidingBlock.getCardinality();
  }

  @Override
  protected HidingBlocksResponse toResponse(HidingBlocks game) throws MciException {
    String fullName = getFullGameName(game);

    EntityRestriction hasBlock;
    EntityRestriction hasHidingBlock;

    try {
      hasBlock = rules.getEntityRestriction(fullName, "hasBlock");
      hasHidingBlock = rules.getEntityRestriction(fullName, "hasHidingBlock");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(HidingBlocks.NAME, e);
    }

    List<Block> existingBlocks;
    List<Block> existingToHideBlocks;
    try {
      existingBlocks = blockProvider.selectBlocksByEntityIdOnProperty(game.getId(), hasBlock.getOnProperty());
      existingToHideBlocks = blockProvider.selectBlocksByEntityIdOnProperty(game.getId(), hasHidingBlock.getOnProperty());
    } catch (ProviderException e) {
      throw GameExceptions.UnableToSolve(HidingBlocks.NAME, e);
    }

    List<BlockItem> blockItems = existingBlocks.stream()
      .map(x -> {
        BlockItem item = new BlockItem();
        item.setId(x.getId());
        item.setRow(x.getRow());
        item.setCol(x.getColumn());

        Block toHide = existingToHideBlocks.stream()
          .filter(y -> y.getId().equals(x.getId()))
          .findFirst()
          .orElse(null);

        item.setHide(toHide != null);
        return item;
      }).collect(Collectors.toList());

    HidingBlocksResponse response = new HidingBlocksResponse(game);
    response.setBlocks(blockItems);
    return response;
  }
}
