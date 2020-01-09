package com.aegean.icsd.mciwebapp.calculations.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Operation {

  private String operator;
  private int first;
  private int second;
  private int result;

  public Operation () { }

  public Operation(int first, int second) throws InterruptedException {
    this.first = first;
    this.second = second;
    setupOperator();
    doCalculation();
  }


  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public int getFirst() {
    return first;
  }

  public void setFirst(int first) {
    this.first = first;
  }

  public int getSecond() {
    return second;
  }

  public void setSecond(int second) {
    this.second = second;
  }

  public int getResult() {
    return this.result;
  }

  public static Operation createRandom(int min, int max, Random rnd) throws InterruptedException {
    Operation current = new Operation();
    int result;
    do {
      current.setFirst(current.getRandomWithExclusion(rnd, min, max));
      current.setSecond(current.getRandomWithExclusion(rnd, min, max));
      current.setOperator(current.getRandomOperator());
      result = current.doCalculation();
      if (result < min) {
        current.setOperator(current.getOppositeOperator());
        result = current.doCalculation();
      }
    } while (result < min || result > max);

    return current;
  }

  public static Operation createRandom(int first, int min, int max, Random rnd) throws InterruptedException {
    Operation current = new Operation();
    int result;
    current.setFirst(first);
    do {
      current.setSecond(current.getRandomWithExclusion(rnd, min, max));
      current.setOperator(current.getRandomOperator());
      result = current.doCalculation();
      if (result < min) {
        current.setOperator(current.getOppositeOperator());
        result = current.doCalculation();
      }
    } while (result < min || result > max);

    return current;
  }


  Integer doCalculation() throws InterruptedException {
    switch (operator) {
      case "+" :
        result = first + second;
        return result;
      case "-" :
        result = first - second;
        return result;
      default:
        throw new InterruptedException("unknown op");
    }
  }

  String getOppositeOperator() {
    switch (operator) {
      case "+" :
        return "-";
      case "-" :
        return "+";
      default:
        return null;
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

  String getRandomOperator() {
    List<String> operators = new ArrayList<>();
    operators.add("+");
    operators.add("-");
    Collections.shuffle(operators, new Random(System.currentTimeMillis()));
    return operators.remove(0);
  }

  void setupOperator() {
    if (second < 0) {
      second = Math.abs(second);
      operator = "-";
    } else {
      operator = "+";
    }
  }
}
