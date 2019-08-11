package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Id;
import com.aegean.icsd.engine.core.annotations.Key;

@Entity(Question.NAME)
public class Question {
  public static final String NAME = "Question";

  @Id
  @DataProperty("hasId")
  private String id;

  @Key
  @DataProperty("hasQuestionDescription")
  private String questionDescription;

  public String getQuestionDescription() {
    return questionDescription;
  }

  public void setQuestionDescription(String questionDescription) {
    this.questionDescription = questionDescription;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
