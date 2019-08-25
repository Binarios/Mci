package com.aegean.icsd.mciwebapp.logicalorder.implementations;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.blocks.beans.Block;
import com.aegean.icsd.mciobjects.blocks.interfaces.IBlockProvider;
import com.aegean.icsd.mciwebapp.logicalorder.beans.LogicalOrder;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class TestService {

  @InjectMocks
  @Spy
  private LogicalOrderSvc svc = new LogicalOrderSvc();

  @Mock
  private IRules rules;

  @Mock
  private IGenerator generator;

  @Mock
  private IBlockProvider blockProvider;

  @Test
  public void testMovementCase1() {
    LogicalOrder game = createGame("UL", 1);
    Block now = createBlock(1,0);
    List<Block> blocks = blocksMap(3, 3);

    Block after = svc.calculateNewPositionedBlock(now , blocks, game);

    Assertions.assertNotNull(after);
    Assertions.assertEquals(2, after.getRow());
    Assertions.assertEquals(2, after.getColumn());
  }

  @Test
  public void testMovementCase2() {
    LogicalOrder game = createGame("DR", 1);
    Block now = createBlock(2,2);
    List<Block> blocks = blocksMap(3, 3);

    Block after = svc.calculateNewPositionedBlock(now , blocks, game);

    Assertions.assertNotNull(after);
    Assertions.assertEquals(0, after.getRow());
    Assertions.assertEquals(1, after.getColumn());
  }

  @Test
  public void testMovementCase3() {
    LogicalOrder game = createGame("L", 2);
    Block now = createBlock(1,1);
    List<Block> blocks = blocksMap(3, 3);

    Block after = svc.calculateNewPositionedBlock(now , blocks, game);

    Assertions.assertNotNull(after);
    Assertions.assertEquals(0, after.getRow());
    Assertions.assertEquals(2, after.getColumn());
  }

  @Test
  public void testMovementCase4() {
    LogicalOrder game = createGame("LD", 2);
    Block now = createBlock(1,1);
    List<Block> blocks = blocksMap(3, 3);

    Block after = svc.calculateNewPositionedBlock(now , blocks, game);

    Assertions.assertNotNull(after);
    Assertions.assertEquals(2, after.getRow());
    Assertions.assertEquals(2, after.getColumn());
  }

  @Test
  public void testMovementCase5() {
    LogicalOrder game = createGame("DL", 2);
    Block now = createBlock(1,1);
    List<Block> blocks = blocksMap(3, 3);

    Block after = svc.calculateNewPositionedBlock(now , blocks, game);

    Assertions.assertNotNull(after);
    Assertions.assertEquals(0, after.getRow());
    Assertions.assertEquals(0, after.getColumn());
  }

  @Test
  public void testMovementCase6() {
    LogicalOrder game = createGame("DR", 2);
    Block now = createBlock(0,1);
    List<Block> blocks = blocksMap(3, 3);

    Block after = svc.calculateNewPositionedBlock(now , blocks, game);

    Assertions.assertNotNull(after);
    Assertions.assertEquals(0, after.getRow());
    Assertions.assertEquals(0, after.getColumn());
  }

  @Test
  public void testMovementCase7() {
    LogicalOrder game = createGame("U", 3);
    Block now = createBlock(0,2);
    List<Block> blocks = blocksMap(3, 3);

    Block after = svc.calculateNewPositionedBlock(now , blocks, game);

    Assertions.assertNotNull(after);
    Assertions.assertEquals(0, after.getRow());
    Assertions.assertEquals(1, after.getColumn());
  }


  private List<Block> blocksMap(int rows, int cols) {
    List<Block> map = new ArrayList<>();
    for (int i = 0; i < rows; i ++) {
      for (int k = 0; k < cols; k ++) {
        Block block = createBlock(i, k);
        map.add(block);
      }
    }
    return map;
  }
  private LogicalOrder createGame(String movement, int step) {
    LogicalOrder game = new LogicalOrder();
    game.setMovement(movement);
    game.setStep(step);
    return game;
  }
  private Block createBlock(int row, int col) {
    Block block = new Block();
    block.setRow(row);
    block.setColumn(col);
    return block;
  }
}
