package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Key;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;

@Entity(Sound.NAME)
public class Sound extends BaseGameObject {
  public static final String NAME = "Sound";

  @DataProperty("hasAssetPath")
  private String path;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
