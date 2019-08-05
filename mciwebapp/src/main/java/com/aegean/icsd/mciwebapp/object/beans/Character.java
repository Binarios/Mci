package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.annotations.DataProperty;
import com.aegean.icsd.engine.annotations.Entity;
import com.aegean.icsd.engine.annotations.Id;
import com.aegean.icsd.engine.annotations.Key;

@Entity(Character.NAME)
public class Character {
  public static final String NAME = "Character";

  @Id
  @DataProperty("hasId")
  private String id;

  @Key
  @DataProperty("hasStringValue")
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
