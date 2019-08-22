package com.aegean.icsd.mciobjects.questions.configurations;

public class QuestionConfiguration {
  private String location;
  private String filename;
  private String delimiter;
  private String choicesDelimiter;
  private int questionIndex;
  private int correctAnswerIndex;
  private int choicesIndex;
  private int categoryIndex;
  private int difficultyIndex;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public int getQuestionIndex() {
    return questionIndex;
  }

  public void setQuestionIndex(int questionIndex) {
    this.questionIndex = questionIndex;
  }

  public int getCorrectAnswerIndex() {
    return correctAnswerIndex;
  }

  public void setCorrectAnswerIndex(int correctAnswerIndex) {
    this.correctAnswerIndex = correctAnswerIndex;
  }

  public int getChoicesIndex() {
    return choicesIndex;
  }

  public void setChoicesIndex(int choicesIndex) {
    this.choicesIndex = choicesIndex;
  }

  public String getChoicesDelimiter() {
    return choicesDelimiter;
  }

  public void setChoicesDelimiter(String choicesDelimiter) {
    this.choicesDelimiter = choicesDelimiter;
  }

  public int getCategoryIndex() {
    return categoryIndex;
  }

  public void setCategoryIndex(int categoryIndex) {
    this.categoryIndex = categoryIndex;
  }

  public int getDifficultyIndex() {
    return difficultyIndex;
  }

  public void setDifficultyIndex(int difficultyIndex) {
    this.difficultyIndex = difficultyIndex;
  }
}
