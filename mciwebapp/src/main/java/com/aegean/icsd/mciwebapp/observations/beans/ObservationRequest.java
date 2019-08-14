package com.aegean.icsd.mciwebapp.observations.beans;

import java.util.Map;

import com.aegean.icsd.mciwebapp.common.beans.Request;

public class ObservationRequest extends Request {

  private Map<String, Integer> solution;

  public Map<String, Integer> getSolution() {
    return solution;
  }

  public void setSolution(Map<String, Integer> solution) {
    this.solution = solution;
  }

}
