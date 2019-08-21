package com.aegean.icsd.mciwebapp.chronorder.beans;

import java.util.List;

import com.aegean.icsd.mciobjects.images.beans.ImageData;
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class ChronologicalOrderResponse extends ServiceResponse<ChronologicalOrder> {

  private List<ImageData> images;

  public ChronologicalOrderResponse(ChronologicalOrder game) {
    super(game);
  }

  public List<ImageData> getImages() {
    return images;
  }

  public void setImages(List<ImageData> images) {
    this.images = images;
  }
}
