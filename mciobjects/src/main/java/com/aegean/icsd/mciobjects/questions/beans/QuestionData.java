package com.aegean.icsd.mciobjects.questions.beans;

import java.util.List;

import com.aegean.icsd.mciobjects.words.beans.Word;

public class QuestionData {
  private Question question;
  private Word answer;
  private List<Word> choices;
  private Word category;

  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  public Word getAnswer() {
    return answer;
  }

  public void setAnswer(Word answer) {
    this.answer = answer;
  }

  public List<Word> getChoices() {
    return choices;
  }

  public void setChoices(List<Word> choices) {
    this.choices = choices;
  }

  public Word getCategory() {
    return category;
  }

  public void setCategory(Word category) {
    this.category = category;
  }
}
