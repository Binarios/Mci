package com.aegean.icsd.mciwebapp.calculations.implementations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class TestCase {

  @Test
  public void testGuessParticipants() throws InterruptedException {
    Random rand = new Random(System.currentTimeMillis());
    String increase = "+";
    String decrease = "-";

    int max = 20;
    int min = 1;
    String[][] operators = new String[5][5];
    Integer[][] numbers = new Integer[5][5];

    List<Integer> excluded = new ArrayList<>();
    boolean searching = true;
    while (searching) {
      numbers[4][4] = getRandomWithExclusion(min, max, excluded.toArray(new Integer[0]));
      try {
        guessOuterParticipants(numbers, operators, increase, decrease, max, min);
        guessInnerParticipants(numbers, operators, increase, decrease, max, min);
      } catch (InterruptedException e) {
        if (!excluded.contains(numbers[4][4])) {
          excluded.add(numbers[4][4]);
          excluded.sort(Comparator.comparingInt(Integer::intValue));
        }
        continue;
      }


      searching = !numbers[0][4].equals(doCalculation(operators[0][1], numbers[0][0], numbers[0][2]));
      searching |= !numbers[4][0].equals(doCalculation(operators[1][0], numbers[0][0], numbers[2][0]));
      searching |= !numbers[2][4].equals(doCalculation(operators[2][1], numbers[2][2], numbers[2][0]));
      searching |= !numbers[4][2].equals(doCalculation(operators[1][2], numbers[2][2], numbers[0][2]));
      searching |= !numbers[4][4].equals(doCalculation(operators[1][4], numbers[0][4], numbers[2][4]));
      searching |= !numbers[4][4].equals(doCalculation(operators[4][1], numbers[4][0], numbers[4][2]));
    }

    System.out.println("\n<===========================>");
    StringBuilder sb = new StringBuilder();
    for (int row = 0; row < 5; row ++) {
      for (int col = 0; col < 5; col ++) {
        if (col == 3 || row == 3) {
          operators[row][col] = "=";
        }
        if (row % 2 == 0 && col % 2 == 0) {
          sb.append(" ").append(numbers[row][col]).append(" ");
        } else {
          if (operators[row][col] == null) {
            sb.append("    ");
          } else {
            sb.append(" ").append(operators[row][col]).append("  ");
          }
        }
      }
      sb.append("\n");
    }
    System.out.println(sb.toString());
    System.out.println("<===========================>");


  }

  Integer doCalculation(String operator, int a , int b) {
    switch (operator) {
      case "-" :
        if ( a < b) return b - a;
        return a - b;
      case "/" :
        if (a < b) return b / a;
        if (b == 0) return 0;
        return a / b;
      default:
        return null;
    }
  }

  void guessInnerParticipants (Integer[][] numbers, String[][] operators,
                               String increasingOp, String decreasingOp,
                               int max, int min) throws InterruptedException {
    boolean searching = true;
    List<Integer> excluded = new ArrayList<>();
    while (searching) {

      numbers[0][0] = getRandomWithExclusion(min, max, excluded.toArray(new Integer[0]));

      operators[0][1] = findOperator(increasingOp, decreasingOp, numbers[0][0], numbers[0][4]);
      numbers[0][2] = doCalculation(decreasingOp, numbers[0][4], numbers[0][0]);

      operators[1][2] = findOperator(increasingOp, decreasingOp, numbers[0][2], numbers[4][2]);
      numbers[2][2] = doCalculation(decreasingOp, numbers[0][2], numbers[4][2]);

      operators[1][0] = findOperator(increasingOp, decreasingOp, numbers[0][0], numbers[4][0]);
      numbers[2][0] = doCalculation(decreasingOp, numbers[0][0], numbers[4][0]);

      operators[2][1] = findOperator(increasingOp, decreasingOp, numbers[2][0], numbers[2][4]);

      if (operators[2][1] == null || operators[1][0] == null
        || operators[1][2] == null || operators[0][1] == null) {
        continue;
      }
      searching = numbers[2][2] != doCalculation(decreasingOp, numbers[2][0], numbers[2][4]).intValue();
    }
  }

  void guessOuterParticipants (Integer[][] numbers, String[][] operators,
                          String increasingOp, String decreasingOp, int max, int min) throws InterruptedException {
    boolean searching = true;
    List<Integer> excludedRow = new ArrayList<>();
    List<Integer> excludedCol = new ArrayList<>();
    int memoryCol = 0;
    int memoryRow = 0;
    while (searching) {

      numbers[0][4] = memoryCol != 0 ? memoryCol
                      : getRandomWithExclusion(min, max, excludedCol.toArray(new Integer[0]));
      numbers[4][0] = memoryRow != 0 ? memoryRow
                      : getRandomWithExclusion(min, max, excludedRow.toArray(new Integer[0]));

      operators[1][4] = findOperator(increasingOp, decreasingOp, numbers[0][4], numbers[4][4]);
      numbers[2][4] = doCalculation(decreasingOp, numbers[0][4], numbers[4][4]);

      operators[4][1] = findOperator(increasingOp, decreasingOp, numbers[4][0], numbers[4][4]);
      numbers[4][2] = doCalculation(decreasingOp, numbers[4][0], numbers[4][4]);

      searching = operators[1][4] == null || operators[4][1] == null;
    }
  }

  int getRandomWithExclusion(int start, int end, Integer... exclude) {
    Random rnd = new Random(System.currentTimeMillis());
    int random = start + rnd.nextInt(end - start + 1 - exclude.length);
    for (int ex : exclude) {
      if (random < ex) {
        break;
      }
      random++;
    }
    return random;
  }

  String findOperator(String increaseOp, String decreaseOp, Integer thisNumber, Integer result) {
    String op;

    if (thisNumber > result) {
      op = decreaseOp;
    } else if (thisNumber < result) {
      op = increaseOp;
    } else if ("*".equals(increaseOp) && "/".equals(decreaseOp)) {
      op = decreaseOp;
    } else {
      op = null;
    }
     return op;
  }

  boolean isModuloZero (Integer thisNumber, Integer thatNumber) {
    if(thisNumber > thatNumber) return thisNumber % thatNumber == 0;
    else if(thisNumber < thatNumber) return thatNumber % thisNumber == 0;
    else return false;
  }
}
