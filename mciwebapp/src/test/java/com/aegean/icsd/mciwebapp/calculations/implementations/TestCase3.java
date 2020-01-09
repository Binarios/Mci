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
public class TestCase3 {

  @Test
  public void testGuessParticipants() throws InterruptedException {
    List<Operation> rowOperations = new ArrayList<>();
    List<Operation> colOperations = new ArrayList<>();
    boolean equals = false;
    int min = 1;
    int max = 20;
    Random rnd = new Random(System.currentTimeMillis());
    while (!equals) {

      Operation row0 = Operation.createRandom(min, max, rnd);
      Operation col0 = Operation.createRandom(row0.getFirst(), min, max, rnd);

      List<Integer> results = new ArrayList<>();
      results.add(row0.getResult());
      results.add(col0.getResult());
      results.sort(Comparator.comparingInt(Integer::intValue));

      int finalResult = getRandomWithExclusion(rnd, min, max, results.toArray(new Integer[0]));

      Operation row4 = new Operation(col0.getResult(), finalResult - col0.getResult());
      Operation col4 = new Operation(row0.getResult(), finalResult - row0.getResult());

      if (row4.getSecond() == row0.getSecond() || col4.getSecond() == col0.getSecond()) {
        continue;
      }

      Operation row2 = new Operation(col0.getSecond(), col4.getSecond() - col0.getSecond());
      Operation col2 = new Operation(row0.getSecond(), row4.getSecond() - row0.getSecond());

      equals = row0.getResult() == col4.getFirst()
              && row2.getResult() == col4.getSecond()
              && row4.getResult() == col4.getResult()
              && col0.getResult() == row4.getFirst()
              && col2.getResult() == row4.getSecond()
              && col4.getResult() == row4.getResult()
              && row2.getSecond() == col2.getSecond();

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
      System.out.println(String.format("%s %s %s = %s", operation.getFirst(), operation.getOperator(), operation.getSecond(), operation.getResult()));
    }
    System.out.println("==========================");
    for (Operation operation : colOperations) {
      System.out.println(String.format("%s %s %s = %s", operation.getFirst(), operation.getOperator(), operation.getSecond(), operation.getResult()));
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

}
