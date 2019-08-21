package com.aegean.icsd.mciwebapp.findthesounds.beans;

import java.util.List;

import com.aegean.icsd.mciobjects.images.beans.ImageData;
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class FindTheSoundResponse extends ServiceResponse<FindTheSound> {

  private List<ImageData> images;
  private String soundId;
  private String soundPath;

  public FindTheSoundResponse(FindTheSound game) {
    super(game);
  }

  public List<ImageData> getImages() {
    return images;
  }

  public void setImages(List<ImageData> images) {
    this.images = images;
  }

  public String getSoundId() {
    return soundId;
  }

  public void setSoundId(String soundId) {
    this.soundId = soundId;
  }

  public String getSoundPath() {
    return soundPath;
  }

  public void setSoundPath(String soundPath) {
    this.soundPath = soundPath;
  }
}
