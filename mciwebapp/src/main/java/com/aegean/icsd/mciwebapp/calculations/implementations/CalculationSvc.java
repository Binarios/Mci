package com.aegean.icsd.mciwebapp.calculations.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.blocks.beans.NumberBlock;
import com.aegean.icsd.mciobjects.blocks.beans.OperatorBlock;
import com.aegean.icsd.mciobjects.blocks.interfaces.IBlockProvider;
import com.aegean.icsd.mciwebapp.calculations.beans.Calculation;
import com.aegean.icsd.mciwebapp.calculations.beans.CalculationResponse;
import com.aegean.icsd.mciwebapp.calculations.interfaces.ICalculationSvc;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;

@Service
public class CalculationSvc extends AbstractGameSvc<Calculation, CalculationResponse> implements ICalculationSvc {

  @Autowired
  private IRules rules;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IBlockProvider blockProvider;

  @Override
  protected void handleDataTypeRestrictions(String fullName, Calculation toCreate) throws MciException {
    EntityRestriction hasSquareRows;
    EntityRestriction hasSquareColumns;
    EntityRestriction calculationsPerInstance;
    EntityRestriction comparisonsPerInstance;

    try {
      hasSquareRows = rules.getEntityRestriction(fullName, "hasSquareRows");
      calculationsPerInstance = rules.getEntityRestriction(fullName, "calculationsPerInstance");
      comparisonsPerInstance = rules.getEntityRestriction(fullName, "comparisonsPerInstance");
      hasSquareColumns = rules.getEntityRestriction(fullName, "hasSquareColumns");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Calculation.NAME, e);
    }

    Long rows = generator.generateLongDataValue(hasSquareRows.getDataRange());
    Long columns = generator.generateLongDataValue(hasSquareColumns.getDataRange());
    Long calculations = generator.generateLongDataValue(calculationsPerInstance.getDataRange());
    Long comparisons = generator.generateLongDataValue(comparisonsPerInstance.getDataRange());

    toCreate.setTotalColumns(Integer.parseInt(columns.toString()));
    toCreate.setTotalRows(Integer.parseInt(rows.toString()));
    toCreate.setCalculationsPerInstance(Integer.parseInt(calculations.toString()));
    toCreate.setComparisonsPerInstance(Integer.parseInt(comparisons.toString()));
  }

  @Override
  protected void handleObjectRestrictions(String fullName, Calculation toCreate) throws MciException {
    EntityRestriction hasHidingBlock;
    EntityRestriction hasNumberBlock;
    EntityRestriction hasOperatorBlock;
    EntityRestriction hasNumberValue;
    EntityRestriction hasCalculationOperators;
    EntityRestriction hasComparisonOperators;

    try {
      hasHidingBlock = rules.getEntityRestriction(fullName, "hasHidingBlock");
      hasNumberBlock = rules.getEntityRestriction(fullName, "hasNumberBlock");
      hasOperatorBlock = rules.getEntityRestriction(fullName, "hasOperatorBlock");
      hasNumberValue = rules.getEntityRestriction(fullName, "hasNumberValue");
      hasCalculationOperators = rules.getEntityRestriction(fullName, "hasCalculationOperators");
      hasComparisonOperators = rules.getEntityRestriction(fullName, "hasComparisonOperators");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Calculation.NAME, e);
    }

    NumberBlock[][] numberBlocks = new NumberBlock[toCreate.getTotalRows()][toCreate.getTotalColumns()];
    OperatorBlock[][] operatorBlocks = new OperatorBlock[toCreate.getTotalRows()][toCreate.getTotalColumns()];

    createGrid(toCreate.getTotalRows(), toCreate.getTotalColumns(), numberBlocks, operatorBlocks);

    //Iterate on rows that we have number blocks
    for (int row = 0; row < toCreate.getTotalRows(); row += 2) {
      List<String> availableOperators = new ArrayList<>();
      Collections.copy(availableOperators, hasCalculationOperators.getOnProperty().getEnumerations());
      Collections.shuffle(availableOperators, new Random(System.currentTimeMillis()));

      List<String> availableComparisons = new ArrayList<>();
      Collections.copy(availableComparisons, hasComparisonOperators.getOnProperty().getEnumerations());
      Collections.shuffle(availableComparisons, new Random(System.currentTimeMillis()));

      NumberBlock[] rowNumberBlocks = numberBlocks[row];
      OperatorBlock[] rowOperatorBlocks = operatorBlocks[row];
      Long first = null;
      Long second = null;

      List<String> calculations = availableOperators.stream().limit( toCreate.getCalculationsPerInstance()).collect(Collectors.toList());
      List<String> comparisons = availableComparisons.stream().limit( toCreate.getComparisonsPerInstance()).collect(Collectors.toList());

      if (calculations.size() + comparisons.size() > rowOperatorBlocks.length) {
        throw GameExceptions.GenerationError(Calculation.NAME, "Total available blocks for operations is" +
          "smaller than the total amount of operations configured");
      }



      for (OperatorBlock operatorBlock : rowOperatorBlocks) {
        if (first == null) {
          first = generator.generateLongDataValue(hasNumberValue.getDataRange());
        }
        if (second == null) {
          second = generator.generateLongDataValue(hasNumberValue.getDataRange());
        }

//        Long result = doCalculation(operator, first, second);
//        if (result == null) {

//        } else {

//        }
      }
    }
  }

  @Override
  protected boolean isValid(Object solution) {
    return false;
  }

  @Override
  protected boolean checkSolution(Calculation game, Object solution) throws MciException {
    return false;
  }

  @Override
  protected CalculationResponse toResponse(Calculation game) throws MciException {
    return null;
  }

  /**
   * This method creates the grid for the game of calculations. It has been observed
   * that the grid used for that game has the following form (OPE = operator, NUM = number):
   *
   *      |NUM|OPE|NUM|OPE|NUM|
   *      |OPE|   |OPE|   |OPE|
   *      |NUM|OPE|NUM|OPE|NUM|
   *      |OPE|   |OPE|   |OPE|
   *      |NUM|OPE|NUM|OPE|NUM|
   *
   * Immediately it is recognized that:
   *    i)all the elements that are located in a cell, whose the sum of coordinates mod 2 equals to 0, are numbers
   *    ii)all the elements that are located in a cell, whose the sum of coordinates mod 2 equals to 1, are operators
   *    iii)all the elements that are located in a cell, whose the row mod 2 equals 1 AND
   *        the sum of coordinates mod 2 equals to 0, are empty. This is because an operator cannot follow another
   *
   * @param rows
   * @param columns
   * @param numberBlocks
   * @param operatorBlocks
   */
  void createGrid(int rows, int columns, NumberBlock[][] numberBlocks, OperatorBlock[][] operatorBlocks) {
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < columns; col++) {
        if (isEmptyBlock(row, col)) {
          continue;
        }
        if (isNumberBlock(row, col)) {
          NumberBlock numberBlock = new NumberBlock();
          numberBlock.setColumn(col);
          numberBlock.setRow(row);
          numberBlocks[row][col] = numberBlock;
        } else {
          OperatorBlock operatorBlock = new OperatorBlock();
          operatorBlock.setColumn(col);
          operatorBlock.setRow(row);
          if (col == columns -1) {
            operatorBlock.setOperator("=");
          }
          operatorBlocks[row][col] = operatorBlock;
        }
      }
    }
  }

  boolean isEmptyBlock(int row, int col) {
    return isPrime(row) && isNumberBlock(row, col);
  }

  boolean isPrime(int i) {
    return i % 2 == 1;
  }

  boolean isNumberBlock(int row, int col) {
    return (col + row) % 2 == 0;
  }


  Long doCalculation(String operator, Long first, Long second) {
    switch (operator) {
      case "+":
        return first + second;
      case "-":
        return first - second;
      case "*":
        return first * second;
      case "/":
        return first / second;
      default:
        return null;
    }
  }

  String doComparison(Long first, Long second) {
    if (first > second) {
      return ">";
    } else if (first < second) {
      return "<";
    } else {
      return "=";
    }
  }

}
