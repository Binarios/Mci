package com.aegean.icsd.mciwebapp.synonyms.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class SynonymResponse extends ServiceResponse<Synonyms> {

  private String word;
  private List<String> choices;

  public SynonymResponse(Synonyms game) {
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
