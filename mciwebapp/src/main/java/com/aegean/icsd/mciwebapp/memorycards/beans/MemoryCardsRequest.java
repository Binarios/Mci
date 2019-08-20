package com.aegean.icsd.mciwebapp.memorycards.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ImageData;
import com.aegean.icsd.mciwebapp.common.beans.Request;

public class MemoryCardsRequest extends Request {
  private List<ImageData> solution;

  public List<ImageData> getSolution() {
    return solution;
  }

  public void setSolution(List<ImageData> solution) {
    this.solution = solution;
  }
}
