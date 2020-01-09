package com.aegean.icsd.mciwebapp.calculations.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class TestCase2 {

  @Test
  public void testGuessParticipants() throws InterruptedException {
    List<Operation> rowOperations = new ArrayList<>();
    List<Operation> colOperations = new ArrayList<>();
    boolean equals = false;
    int min = 1;
    int max = 20;
    Random rnd = new Random(System.currentTimeMillis());
    while (!equals) {
      Operation row0 = new Operation();
      int row0Result;
      do {
        row0.setFirst(getRandomWithExclusion(rnd, min, max));
        row0.setSecond(getRandomWithExclusion(rnd, min, max));
        row0.setOperator(getRandomOperator());
        row0Result = doCalculation(row0);
        if (row0Result < min) {
          row0.setOperator(getOppositeOperator(row0.getOperator()));
          row0Result = doCalculation(row0);
        }
      } while (row0Result < min || row0Result > max);


      Operation col0 = new Operation();
      col0.setFirst(row0.getFirst());
      int col0Result;
      do {
        col0.setSecond(getRandomWithExclusion(rnd, min, max));
        col0.setOperator(getRandomOperator());
        col0Result = doCalculation(col0);
        if (col0Result < min) {
          col0.setOperator(getOppositeOperator(col0.getOperator()));
          col0Result = doCalculation(col0);
        }

      } while (col0Result < min || col0Result > max);

      List<Integer> results = new ArrayList<>();
      results.add(row0Result);
      results.add(col0Result);
      results.sort(Comparator.comparingInt(Integer::intValue));

      int finalResult = getRandomWithExclusion(rnd, min, max, results.toArray(new Integer[0]));

      Operation row4 = new Operation();
      row4.setFirst(col0Result);
      row4.setSecond(finalResult - col0Result);
      setupOperator(row4);

      if (row4.getSecond() == row0.getSecond()) {
        continue;
      }

      Operation col4 = new Operation();
      col4.setFirst(row0Result);
      col4.setSecond(finalResult - row0Result);
      setupOperator(col4);

      if (col4.getSecond() == col0.getSecond()) {
        continue;
      }

      Operation row2 = new Operation();
      row2.setFirst(col0.getSecond());
      row2.setSecond(col4.getSecond() - row2.getFirst());
      setupOperator(row2);

      Operation col2 = new Operation();
      col2.setFirst(row0.getSecond());
      col2.setSecond(row4.getSecond() - col2.getFirst());
      setupOperator(col2);

      equals = doCalculation(col0) == row4.getFirst() && doCalculation(row4).equals(doCalculation(col4))
              && doCalculation(row2) == col4.getSecond() && doCalculation(col2).equals(row4.getSecond())
              && doCalculation(row0) == col4.getSecond();

      if (equals) {
        rowOperations.add(row0);
        rowOperations.add(row2);
        rowOperations.add(row4);

        colOperations.add(col0);
        colOperations.add(col2);
        colOperations.add(col4);
      }
    }

    for (Operation operation : rowOperations) {
      System.out.println(String.format("%s %s %s = %s", operation.getFirst(), operation.getOperator(), operation.getSecond(), doCalculation(operation)));
    }
    System.out.println("==========================");
    for (Operation operation : colOperations) {
      System.out.println(String.format("%s %s %s = %s", operation.getFirst(), operation.getOperator(), operation.getSecond(), doCalculation(operation)));
    }


  }

  String getRandomOperator() {
    List<String> operators = new ArrayList<>();
    operators.add("+");
    operators.add("-");
    Collections.shuffle(operators, new Random(System.currentTimeMillis()));
    return operators.remove(0);
  }

  String getOppositeOperator(String op) {
    switch (op) {
      case "+" :
        return "-";
      case "-" :
        return "+";
      default:
        return null;
    }
  }

  Integer doCalculation(Operation op) throws InterruptedException {
    switch (op.getOperator()) {
      case "+" :
        return op.getFirst() + op.getSecond();
      case "-" :
        return op.getFirst() - op.getSecond();
      default:
        throw new InterruptedException("unknown op");
    }
  }

  int getRandomWithExclusion(Random rnd , int start, int end, Integer... exclude) {
    int random = start + rnd.nextInt(end - start + 1 - exclude.length);
    for (int ex : exclude) {
      if (random < ex) {
        break;
      }
      random++;
    }
    return random;
  }

  void setupOperator(Operation operation) {
    if (operation.getSecond() < 0) {
      operation.setSecond(Math.abs(operation.getSecond()));
      operation.setOperator("-");
    } else {
      operation.setOperator("+");
    }
  }


}
