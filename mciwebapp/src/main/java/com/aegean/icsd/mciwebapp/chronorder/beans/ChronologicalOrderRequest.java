package com.aegean.icsd.mciwebapp.chronorder.beans;

import java.util.List;

import com.aegean.icsd.mciobjects.images.beans.ImageData;
import com.aegean.icsd.mciwebapp.common.beans.Request;

public class ChronologicalOrderRequest extends Request {
  private List<ImageData> solution;

  public List<ImageData> getSolution() {
    return solution;
  }

  public void setSolution(List<ImageData> solution) {
    this.solution = solution;
  }
}
