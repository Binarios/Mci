package com.aegean.icsd.engine.generator.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Id;

public class BaseGameObject {
  @Id
  @DataProperty("hasId")
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
