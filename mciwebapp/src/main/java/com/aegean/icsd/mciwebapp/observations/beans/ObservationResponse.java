package com.aegean.icsd.mciwebapp.observations.beans;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

import openllet.core.utils.Bool;

public class ObservationResponse extends ServiceResponse<Observation> {
  private List<ObservationItem> items;
  private List<String> words;

  public ObservationResponse(Observation observation) {
    super(observation);
  }

  public List<ObservationItem> getItems() {
    return items;
  }

  public void setItems(List<ObservationItem> items) {
    this.items = items;
  }

  public List<String> getWords() {
    return words;
  }

  public void setWords(List<String> words) {
    this.words = words;
  }

}
