package com.aegean.icsd.ontology.queries;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class TestSelectQuery {

  @Test
  public void testBuilder() {
    SelectQuery query = new SelectQuery.Builder()
      .select("?s", "?p", "?o")
      .where("?s", "?p", "?o")
      .build();
    String expected = "SELECT ?s ?p ?o \nWHERE {\n?s ?p ?o .\n}\n";
    String actual = query.getCommand();

    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void testBuilderWithManyWhere() {
    SelectQuery query = new SelectQuery.Builder()
      .select("?s", "?p", "?o")
      .where("?s", "?p", "?o")
      .where("?s", "mci:hasPredicate", "mci:Object")
      .build();
    String expected = "SELECT ?s ?p ?o \nWHERE {\n?s ?p ?o;\n\tmci:hasPredicate mci:Object .\n}\n";
    String actual = query.getCommand();

    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void testBuilderWithWhereAndRegexFilter() {
    SelectQuery query = new SelectQuery.Builder()
      .select("?s", "?p", "?o")
      .where("?s", "?p", "?o")
      .regexFilter("testVal", "pattern")
      .build();
    String expected = "SELECT ?s ?p ?o \nWHERE {\n?s ?p ?o .\nFILTER regex(testVal, pattern)\n}\n";
    String actual = query.getCommand();

    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void testBuilderWithWhereAndRegexFilterFlag() {
    SelectQuery query = new SelectQuery.Builder()
      .select("?s", "?p", "?o")
      .where("?s", "?p", "?o")
      .regexFilter("testVal", "pattern", "i")
      .build();
    String expected = "SELECT ?s ?p ?o \nWHERE {\n?s ?p ?o .\nFILTER regex(testVal, pattern, i)\n}\n";
    String actual = query.getCommand();

    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void testBuilderWithManyWhereAndRegexFilterFlag() {
    SelectQuery query = new SelectQuery.Builder()
      .select("?s", "?p", "?o")
      .where("?s", "?p", "?o")
      .where("?s", "mci:hasPredicate", "mci:Object")
      .regexFilter("testVal", "pattern", "i")
      .build();
    String expected = "SELECT ?s ?p ?o \nWHERE {\n?s ?p ?o;\n\tmci:hasPredicate mci:Object .\nFILTER regex(testVal, pattern, i)\n}\n";
    String actual = query.getCommand();

    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void testSelectWithOutSpecialCharacter() {
    SelectQuery query = new SelectQuery.Builder()
      .select("s", "p", "o")
      .build();
    String expected = "SELECT ?s ?p ?o \nWHERE {\n}\n";
    String actual = query.getCommand();

    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void testSelectWithDollarCharacter() {
    SelectQuery query = new SelectQuery.Builder()
      .select("$s", "$p", "$o")
      .build();
    String expected = "SELECT ?s ?p ?o \nWHERE {\n}\n";
    String actual = query.getCommand();

    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void testFilterOperatorGT() {
    SelectQuery query = new SelectQuery.Builder()
      .select("$s", "$p", "$o")
      .filter("o", SelectQuery.Builder.Operator.GT, "2")
      .build();
    String expected = "SELECT ?s ?p ?o \nWHERE {\nFILTER (o > 2)\n}\n";
    String actual = query.getCommand();

    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void testFilterOperatorLT() {
    SelectQuery query = new SelectQuery.Builder()
      .select("$s", "$p", "$o")
      .filter("o", SelectQuery.Builder.Operator.LT, "2")
      .build();
    String expected = "SELECT ?s ?p ?o \nWHERE {\nFILTER (o < 2)\n}\n";
    String actual = query.getCommand();

    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void testFilterOperatorEQ() {
    SelectQuery query = new SelectQuery.Builder()
      .select("$s", "$p", "$o")
      .filter("o", SelectQuery.Builder.Operator.EQ, "2")
      .build();
    String expected = "SELECT ?s ?p ?o \nWHERE {\nFILTER (o = 2)\n}\n";
    String actual = query.getCommand();

    Assertions.assertEquals(expected, actual);
  }
}
