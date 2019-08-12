package com.aegean.icsd.mciwebapp.object.beans;

import java.util.List;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Key;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;

@Entity(LabyrinthBlock.NAME)
public class LabyrinthBlock extends BaseGameObject {
  public static final String NAME = "LabyrinthBlock";

  @Key
  @DataProperty("hasBorder")
  public List<String> borders;

  public List<String> getBorders () {
    return this.borders;
  }

  public void setBorders(List<String> borders) {
    this.borders = borders;
  }
}
