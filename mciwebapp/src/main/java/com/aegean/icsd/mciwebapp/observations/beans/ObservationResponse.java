package com.aegean.icsd.mciwebapp.observations.beans;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;

import openllet.core.utils.Bool;

public class ObservationResponse {
  private Observation observation;
  private Boolean solved;
  private List<ObservationItem> items;
  private List<String> words;

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

  public Observation getObservation() {
    return observation;
  }

  public void setObservation(Observation observation) {
    this.observation = observation;
  }

  public Boolean getSolved() {
    return solved;
  }

  public void setSolved(Boolean solved) {
    this.solved = solved;
  }
}
