package com.aegean.icsd.mciwebapp.observations.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.common.beans.BaseGame;

@Entity(Observation.NAME)
public class Observation extends BaseGame {
  public static final String NAME = "Observation";

  @DataProperty("hasTotalImages")
  private Integer totalImages;

  public Integer getTotalImages() {
    return totalImages;
  }

  public void setTotalImages(Integer totalImages) {
    this.totalImages = totalImages;
  }
}
