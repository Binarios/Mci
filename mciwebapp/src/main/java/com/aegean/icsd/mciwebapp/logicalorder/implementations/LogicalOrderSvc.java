package com.aegean.icsd.mciwebapp.logicalorder.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.blocks.beans.Block;
import com.aegean.icsd.mciobjects.blocks.beans.BlockSet;
import com.aegean.icsd.mciobjects.blocks.interfaces.IBlockProvider;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.logicalorder.beans.BlockItem;
import com.aegean.icsd.mciwebapp.logicalorder.beans.BlockSetItem;
import com.aegean.icsd.mciwebapp.logicalorder.beans.LogicalOrder;
import com.aegean.icsd.mciwebapp.logicalorder.beans.LogicalOrderResponse;
import com.aegean.icsd.mciwebapp.logicalorder.beans.SolutionItem;
import com.aegean.icsd.mciwebapp.logicalorder.interfaces.ILogicalOrderSvc;

@Service
public class LogicalOrderSvc extends AbstractGameSvc<LogicalOrder, LogicalOrderResponse> implements ILogicalOrderSvc {

  @Autowired
  private IRules rules;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IBlockProvider blockProvider;

  @Override
  protected void handleDataTypeRestrictions(String fullName, LogicalOrder toCreate) throws MciException {
    EntityRestriction hasStep;
    EntityRestriction hasSquareColumns;
    EntityRestriction hasSquareRows;
    EntityRestriction hasTotalMovingBlocks;
    EntityRestriction hasMovement;
    EntityRestriction choices;

    try {
      hasStep = rules.getEntityRestriction(fullName, "hasStep");
      hasSquareColumns = rules.getEntityRestriction(fullName, "hasSquareColumns");
      hasSquareRows = rules.getEntityRestriction(fullName, "hasSquareRows");
      hasTotalMovingBlocks = rules.getEntityRestriction(fullName, "hasTotalMovingBlocks");
      hasMovement = rules.getEntityRestriction(fullName, "hasMovement");
      choices = rules.getEntityRestriction(fullName, "choices");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(LogicalOrder.NAME, e);
    }

    Long step = generator.generateLongDataValue(hasStep.getDataRange());
    Long rows = generator.generateLongDataValue(hasSquareRows.getDataRange());
    Long columns = generator.generateLongDataValue(hasSquareColumns.getDataRange());
    Long movingBlocks = generator.generateLongDataValue(hasTotalMovingBlocks.getDataRange());
    Long nbOfChoices = generator.generateLongDataValue(choices.getDataRange());

    Collections.shuffle(hasMovement.getOnProperty().getEnumerations(), new Random(System.currentTimeMillis()));
    String movement = hasMovement.getOnProperty().getEnumerations().get(0);

    toCreate.setStep(Integer.parseInt(step.toString()));
    toCreate.setRows(Integer.parseInt(rows.toString()));
    toCreate.setColumns(Integer.parseInt(columns.toString()));
    toCreate.setTotalMovingBlocks(Integer.parseInt(movingBlocks.toString()));
    toCreate.setChoices(Integer.parseInt(nbOfChoices.toString()));
    toCreate.setMovement(movement);
  }

  @Override
  protected void handleObjectRestrictions(String fullName, LogicalOrder toCreate) throws MciException {
    EntityRestriction hasBlockSet;
    EntityRestriction hasCorrectBlockSet;
    try {
      hasBlockSet = rules.getEntityRestriction(fullName, "hasBlockSet");
      hasCorrectBlockSet = rules.getEntityRestriction(fullName, "hasCorrectBlockSet");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(LogicalOrder.NAME, e);
    }

    if (hasCorrectBlockSet.getCardinality() >= hasBlockSet.getCardinality()
        || hasCorrectBlockSet.getCardinality() > toCreate.getChoices()
        || hasCorrectBlockSet.getCardinality() + toCreate.getChoices() >= hasBlockSet.getCardinality()) {
      throw GameExceptions.GenerationError(LogicalOrder.NAME, "Problem with the rules of the game");
    }

    List<BlockSet> blockSets;
    try {
      blockSets = blockProvider.getNewBlockSets(toCreate.getRows(), toCreate.getColumns(), hasBlockSet.getCardinality());

      int remainingChoices = toCreate.getChoices() - hasCorrectBlockSet.getCardinality();
      for (int i = 0; i < remainingChoices; i++) {
        BlockSet choice = blockSets.remove(0);
        List<Block> randomMoveBlocks = calculatePositions(choice.getBlocks(), null, toCreate);
        blockProvider.updateMovingBlockFor(choice, randomMoveBlocks);
        createObjRelation(toCreate, choice, hasBlockSet.getOnProperty());
      }

      int order = 0;
      List<Block> movingBlocks = null;
      for (BlockSet set : blockSets) {
        movingBlocks = calculatePositions(set.getBlocks(), movingBlocks, toCreate);
        set.setOrder(order);
        blockProvider.updateMovingBlockFor(set, movingBlocks);
        order++;
      }
      blockProvider.orderBlockSets(blockSets);
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(LogicalOrder.NAME, e);
    }

    blockSets.sort(Comparator.comparingInt(BlockSet::getOrder));
    List<BlockSet> asked = blockSets.subList(blockSets.size() - hasCorrectBlockSet.getCardinality(), blockSets.size());

    createObjRelation(toCreate, asked, hasCorrectBlockSet.getOnProperty());
    createObjRelation(toCreate, blockSets, hasBlockSet.getOnProperty());
  }

  @Override
  protected boolean isValid(Object solution) {
    List<SolutionItem> casted = (List) solution;
    return casted != null && !casted.isEmpty();
  }

  @Override
  protected boolean checkSolution(LogicalOrder game, Object solution) throws MciException {
    List<SolutionItem> casted = (List) solution;
    casted.sort(Comparator.comparingInt(SolutionItem::getOrder));

    EntityRestriction hasCorrectBlockSet;
    try {
      hasCorrectBlockSet = rules.getEntityRestriction(getFullGameName(game), "hasCorrectBlockSet");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(LogicalOrder.NAME, e);
    }

    List<BlockSet> correctSets;
    try {
      correctSets = blockProvider.selectBlockSetsByEntityIdOnProperty(game.getId(), hasCorrectBlockSet.getOnProperty());
    } catch (ProviderException e) {
      throw  GameExceptions.UnableToResponse(LogicalOrder.NAME, e);
    }
    boolean solved = true;

    for (BlockSet correctSet : correctSets) {
      solved &= casted.stream()
        .anyMatch(x -> x.getId().equals(correctSet.getId()) && x.getOrder().equals(correctSet.getOrder()));
    }

    return solved;
  }

  @Override
  protected LogicalOrderResponse toResponse(LogicalOrder game) throws MciException {
    EntityRestriction hasBlockSet;
    EntityRestriction hasCorrectBlockSet;
    try {
      hasBlockSet = rules.getEntityRestriction(getFullGameName(game), "hasBlockSet");
      hasCorrectBlockSet = rules.getEntityRestriction(getFullGameName(game), "hasCorrectBlockSet");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(LogicalOrder.NAME, e);
    }

    List<BlockSet> allBlockSets;
    List<BlockSet> correctSets;
    try {
      allBlockSets = blockProvider.selectBlockSetsByEntityIdOnProperty(game.getId(), hasBlockSet.getOnProperty());
      correctSets = blockProvider.selectBlockSetsByEntityIdOnProperty(game.getId(), hasCorrectBlockSet.getOnProperty());
    } catch (ProviderException e) {
      throw  GameExceptions.UnableToResponse(LogicalOrder.NAME, e);
    }

    allBlockSets.removeIf(x -> correctSets.stream().anyMatch(y -> y.getId().equals(x.getId())));

    List<BlockSet> choiceSets = allBlockSets.stream().filter(x -> x.getOrder() == null).collect(Collectors.toList());
    allBlockSets.removeIf(x -> x.getOrder() == null);

    List<BlockSetItem> sequence = allBlockSets.stream()
      .map(this::toBlockSetItem)
      .sorted(Comparator.comparingInt(BlockSetItem::getOrder))
      .collect(Collectors.toList());

    choiceSets.addAll(correctSets);
    List<BlockSetItem> choices = choiceSets.stream()
      .map(this::toBlockSetItem)
      .collect(Collectors.toList());

    Collections.shuffle(choices, new Random(System.currentTimeMillis()));

    LogicalOrderResponse response = new LogicalOrderResponse(game);
    response.setSequence(sequence);
    response.setChoices(choices);
    return response;
  }

  List<Block> calculatePositions(List<Block> blocks, List<Block> previousMovingBlocks, LogicalOrder game) {

    List<Block> newMoved = new ArrayList<>();
    List<Block> previousState = previousMovingBlocks;
    if (previousState == null || previousState.isEmpty()) {
      Collections.shuffle(blocks, new Random(System.currentTimeMillis()));
      previousState =  blocks.stream().limit(game.getTotalMovingBlocks()).collect(Collectors.toList());
    }

    for (Block blockBefore : previousState) {
      Block moved = calculateNewPositionedBlock(blockBefore, blocks, game);
      blocks.stream()
        .filter(x -> moved.getColumn().equals(x.getColumn())
                  && moved.getRow().equals(x.getRow()))
        .findFirst()
        .ifPresent(newMoved::add);
    }

    return newMoved;
  }

  /**
   * The way the new position is calculated is as follows.
   * We create two separate sorted lists, one is sorted asc by column and the other by row.
   * What we want to achieve is to do the requested movement in both axes or in both sorted lists that we just created.
   *
   * First we go through the columns list. After we find the index of the {@code blockBefore} we apply the horizontal
   * movement n times, where n is the number of {@link LogicalOrder#getStep()}, to the index itself.
   * If the new index is equals or above zero,
   *      then we retrieve the item of the sorted list at that position.
   * If the new index is negative,
   *      then we add the size {@code blocks} and we retrieve the item of the sorted
   *      list at that position.
   * if the new index is greater than the size {@code blocks}
   *      then we subtract the size of {@code blocks}
   *      and we retrieve the item of the sorted list at that position
   *
   * Once we have retrieved the block from the sorted columns list, we find its index in the sorted row list. Then we
   * repeat the same process but for the row sorted list.
   *
   * @param blockBefore
   * @param blocks
   * @param game
   * @return
   */
  Block calculateNewPositionedBlock(Block blockBefore, List<Block> blocks, LogicalOrder game) {
    List<Block> columnsMap = new ArrayList<>(blocks);
    columnsMap.sort(Comparator.comparingInt(Block::getRow));

    List<Block> rowsMap = new ArrayList<>(blocks);
    rowsMap.sort(Comparator.comparingInt(Block::getColumn));

    int step = game.getStep();
    String[] movements = game.getMovement().split("");

    Block finalBlock = new Block();
    finalBlock.setRow(blockBefore.getRow());
    finalBlock.setColumn(blockBefore.getColumn());

    for (String movement : movements) {
      if (movesHorizontal(movement)) {
        finalBlock = moveBlock(movement, step, finalBlock, columnsMap);
      } else {
        finalBlock = moveBlock(movement, step, finalBlock, rowsMap);
      }
    }

    return finalBlock;
  }

  Block moveBlock(String movement, int step, Block block, List<Block> blocks) {
    Block finalBlock;

    Block found = blocks.stream()
      .filter(x -> x.getRow().equals(block.getRow()) && x.getColumn().equals(block.getColumn()))
      .findFirst()
      .orElse(null);

    int index = blocks.indexOf(found);
    int position;

    if (movesBackward(movement)) {
      position = index - step;
      if (position >= 0) {
        finalBlock = blocks.get(position);
      } else {
        finalBlock = blocks.get(position + blocks.size());
      }
    } else {
      position = index + step;
      if (position >= blocks.size()) {
        finalBlock = blocks.get(position - blocks.size());
      } else {
        finalBlock = blocks.get(position);
      }
    }

    return finalBlock;
  }

  boolean movesHorizontal(String movement) {
    return movesHorizontalForward(movement) || movesHorizontalBackward(movement);
  }

  boolean movesBackward(String movement) {
    return movesHorizontalBackward(movement) || movesVerticalUp(movement);
  }

  boolean movesHorizontalForward(String movement) {
    return movement.contains("R");
  }

  boolean movesHorizontalBackward(String movement) {
    return movement.contains("L");
  }

  boolean movesVerticalUp(String movement) {
    return movement.contains("U");
  }

  BlockSetItem toBlockSetItem(BlockSet set) {
    BlockSetItem item = new BlockSetItem();
    item.setId(set.getId());
    item.setOrder(set.getOrder());
    item.setBlocks(getBlockItems(set));
    return item;
  }

  List<BlockItem> getBlockItems(BlockSet set) {

    List<BlockItem> blockItems = new ArrayList<>();
    for (Block block : set.getBlocks()) {
      boolean isMoved = set.getMovingBlocks().stream().anyMatch(moving -> moving.getId().equals(block.getId()));
      BlockItem blockItem = toBlockItem(block);
      blockItem.setMoved(isMoved);

      blockItems.add(blockItem);
    }
    return blockItems;
  }

  BlockItem toBlockItem(Block block) {
    BlockItem blockItem = new BlockItem();
    blockItem.setCol(block.getColumn());
    blockItem.setRow(block.getRow());
    blockItem.setId(block.getId());
    return blockItem;
  }
}
