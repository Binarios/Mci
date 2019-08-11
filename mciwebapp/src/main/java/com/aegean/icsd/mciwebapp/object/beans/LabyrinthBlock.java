package com.aegean.icsd.mciwebapp.object.beans;

import java.util.List;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Id;
import com.aegean.icsd.engine.core.annotations.Key;

@Entity(LabyrinthBlock.NAME)
public class LabyrinthBlock {
  public static final String NAME = "LabyrinthBlock";

  @Id
  @DataProperty("hasId")
  private String id;

  @Key
  @DataProperty("hasBorder")
  public List<String> borders;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getBorders () {
    return this.borders;
  }

  public void setBorders(List<String> borders) {
    this.borders = borders;
  }
}
