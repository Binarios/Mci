package com.aegean.icsd.mciwebapp.memorycards.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ImageData;
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class MemoryCardsResponse extends ServiceResponse<MemoryCards> {

  private List<ImageData> images;

  public MemoryCardsResponse(MemoryCards game) {
    super(game);
  }

  public List<ImageData> getImages() {
    return images;
  }

  public void setImages(List<ImageData> images) {
    this.images = images;
  }
}
