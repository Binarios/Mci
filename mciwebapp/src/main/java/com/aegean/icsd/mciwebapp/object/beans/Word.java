package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.annotations.DataProperty;
import com.aegean.icsd.engine.annotations.Entity;
import com.aegean.icsd.engine.annotations.Id;
import com.aegean.icsd.engine.annotations.Key;

@Entity(Word.NAME)
public class Word {
  public static final String NAME = "Word";

  @Id
  @DataProperty("hasId")
  private String id;

  @Key
  private String value;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
