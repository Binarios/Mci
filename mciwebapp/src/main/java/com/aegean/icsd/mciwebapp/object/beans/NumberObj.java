package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Key;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;

@Entity(NumberObj.NAME)
public class NumberObj extends BaseGameObject {
  public static final String NAME = "Number";

  @Key
  @DataProperty("hasNumberValue")
  private Long value;

  public Long getValue() {
    return value;
  }

  public void setValue(Long value) {
    this.value = value;
  }

}
