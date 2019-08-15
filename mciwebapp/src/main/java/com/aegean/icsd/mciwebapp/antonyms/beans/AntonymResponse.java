package com.aegean.icsd.mciwebapp.antonyms.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class AntonymResponse extends ServiceResponse<Antonyms> {

  private String word;
  private List<String> choices;

  public AntonymResponse(Antonyms game) {
    super(game);
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public List<String> getChoices() {
    return choices;
  }

  public void setChoices(List<String> choices) {
    this.choices = choices;
  }
}
