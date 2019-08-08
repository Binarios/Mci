package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Id;
import com.aegean.icsd.engine.core.annotations.Key;

@Entity(CharacterObj.NAME)
public class CharacterObj {
  public static final String NAME = "Character";

  @Id
  @DataProperty("hasId")
  private String id;

  @Key
  @DataProperty("hasStringValue")
  private Character value;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Character getValue() {
    return value;
  }

  public void setValue(Character value) {
    this.value = value;
  }
}
