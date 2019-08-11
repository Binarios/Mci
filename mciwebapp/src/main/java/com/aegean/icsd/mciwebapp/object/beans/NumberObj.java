package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Id;
import com.aegean.icsd.engine.core.annotations.Key;

@Entity(NumberObj.NAME)
public class NumberObj {
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
