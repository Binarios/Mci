package com.aegean.icsd.mciwebapp.questions.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class QuestionsResponse extends ServiceResponse<Questions> {

  private String question;
  private String category;
  private List<String> choices;

  public QuestionsResponse(Questions game) {
    super(game);
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public List<String> getChoices() {
    return choices;
  }

  public void setChoices(List<String> choices) {
    this.choices = choices;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }
}
