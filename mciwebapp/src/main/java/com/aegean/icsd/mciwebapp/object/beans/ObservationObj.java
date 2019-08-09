package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Id;
import com.aegean.icsd.engine.core.annotations.Key;

@Entity(ObservationObj.NAME)
public class ObservationObj {
  public static final String NAME = "ObservationObj";

  @Id
  @DataProperty("hasId")
  private String id;

  @Key
  private String imageId;

  @DataProperty("hasTotalImages")
  private int nbOfImages;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getNbOfImages() {
    return nbOfImages;
  }

  public void setNbOfImages(int nbOfImages) {
    this.nbOfImages = nbOfImages;
  }

  public String getImageId() {
    return imageId;
  }

  public void setImageId(String imageId) {
    this.imageId = imageId;
  }
}
