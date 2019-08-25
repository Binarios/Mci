package com.aegean.icsd.mciobjects.images.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Id;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;

@Entity(Image.NAME)
public class Image extends BaseGameObject {
  public static final String NAME = "Image";

  @Id(autoGenerated = true)
  @DataProperty("hasId")
  private String id;

  @DataProperty("hasAssetPath")
  private String path;

  @DataProperty("hasChronologicalOrder")
  private Boolean ordered;

  @DataProperty("isSoundAssociated")
  private Boolean soundAssociated;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public void setOrdered(Boolean ordered) {
    this.ordered = ordered;
  }

  public Boolean isOrdered() {
    return this.ordered;
  }

  public void setSoundAssociated(Boolean soundAssociated) {
    this.soundAssociated = soundAssociated;
  }

  public Boolean isSoundAssociated() {
    return this.soundAssociated;
  }
}
