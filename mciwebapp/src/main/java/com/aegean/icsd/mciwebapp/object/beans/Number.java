package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.annotations.DataProperty;
import com.aegean.icsd.engine.annotations.Entity;
import com.aegean.icsd.engine.annotations.Id;
import com.aegean.icsd.engine.annotations.Key;

@Entity(Number.NAME)
public class Number {
  public static final String NAME = "Number";

  @Id
  @DataProperty("hasId")
  private String id;

  @Key
  @DataProperty("hasNumberValue")
  public Long value;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
