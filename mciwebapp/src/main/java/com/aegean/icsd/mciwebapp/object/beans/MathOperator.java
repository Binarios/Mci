package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.annotations.DataProperty;
import com.aegean.icsd.engine.annotations.Entity;
import com.aegean.icsd.engine.annotations.Id;
import com.aegean.icsd.engine.annotations.Key;

@Entity(MathOperator.NAME)
public class MathOperator {
  public static final String NAME = "MathOperator";

  @Id
  @DataProperty("hasId")
  private String id;

  @Key
  private String symbolName;

  @DataProperty("hasSymbol")
  private String symbol;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSymbolName() {
    return symbolName;
  }

  public void setSymbolName(String symbolName) {
    this.symbolName = symbolName;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }
}
