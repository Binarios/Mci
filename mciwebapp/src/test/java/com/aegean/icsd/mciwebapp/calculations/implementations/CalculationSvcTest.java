package com.aegean.icsd.mciwebapp.calculations.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.mciobjects.blocks.beans.NumberBlock;
import com.aegean.icsd.mciobjects.blocks.beans.OperatorBlock;


@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
class CalculationSvcTest {

  @InjectMocks
  @Spy
  private CalculationSvc svc = new CalculationSvc();


  @Test
  void testCreateGrid() {
    int rows = 5;
    int columns = 5;

    NumberBlock[][] numberBlocks = new NumberBlock[rows][columns];
    OperatorBlock[][] operatorBlocks = new OperatorBlock[rows][columns];

    svc.createGrid(rows, columns, numberBlocks, operatorBlocks);

    int numberBlocksTotal = 0;
    int operatorBlocksTotal = 0;

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < columns; col++) {
        if (numberBlocks[row][col] != null) {
          numberBlocksTotal++;
        }
        if (operatorBlocks[row][col] != null) {
          operatorBlocksTotal++;
        }
      }
    }

    Assertions.assertEquals(9, numberBlocksTotal);
    Assertions.assertEquals(12, operatorBlocksTotal);

  }


  @Test
  void testaki() {
    Random rand = new Random(System.currentTimeMillis());
    boolean searching = true;
    int count = 0;
    int row0col0 = rand.nextInt(10) + 1;
    int row0col1 = rand.nextInt(10) + 1;
    int row1col0= rand.nextInt(10) + 1;
    int row1col1 = rand.nextInt(10) + 1;

    Operation row1 = createOperation("+", 0, 0);
    Operation row2 = createOperation("-", 0, 0);
    Operation col1 = createOperation("-", 0, 0);
    Operation col2 = createOperation("+", 0, 0);

    List<Operation> operations = new ArrayList<>();
    operations.add(row1);
    operations.add(row2);
    operations.add(col1);
    operations.add(col2);

    int rows = 5;
    int columns = 5;

    NumberBlock[][] numberBlocks = new NumberBlock[rows][columns];
    OperatorBlock[][] operatorBlocks = new OperatorBlock[rows][columns];

    svc.createGrid(rows, columns, numberBlocks, operatorBlocks);

    while(searching) {
      row0col0 = rand.nextInt(10) + 1;
      row0col1 = rand.nextInt(10) + 1;
      row1col0 = rand.nextInt(10) + 1;
      row1col1 = rand.nextInt(10) + 1;



      int f = add(add(row0col0, row0col1), add(row1col0, row1col1));
      int f1 = add(add(row0col0, row1col0), add(row0col1, row1col1));
      int diff = f - f1;
      searching = diff != 0;

      count ++;
    }

    System.out.println("total tries: " + count);
    System.out.println("row0col0 = " + row0col0);
    System.out.println("row0col1 = " + row0col1);
    System.out.println("row1col0 = " + row1col0);
    System.out.println("row1col1 = " + row1col1);
  }

  int add(int a, int b) {
    return a+b;
  }
  int subtract(int a, int b) {
    return a-b;
  }

  Operation createOperation (String operator, int a, int b) {
    Operation operation = new Operation();
    operation.setFirst(a);
    operation.setSecond(b);
    operation.setOperator(operator);
    return operation;
  }

}