package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Key;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;

@Entity(Word.NAME)
public class Word extends BaseGameObject {
  public static final String NAME = "Word";

  @Key
  @DataProperty("hasStringValue")
  private String value;

  @DataProperty("hasWordLength")
  private Integer length;

  @DataProperty("isSynonym")
  private Boolean synonym;

  @DataProperty("isAntonym")
  private Boolean antonym;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public Boolean isAntonym() {
    return antonym;
  }

  public void setAntonym(Boolean antonym) {
    this.antonym = antonym;
  }

  public Boolean isSynonym() {
    return synonym;
  }

  public void setSynonym(Boolean synonym) {
    this.synonym = synonym;
  }
}
