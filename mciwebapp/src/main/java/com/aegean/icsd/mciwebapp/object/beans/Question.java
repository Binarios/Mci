package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Key;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;

@Entity(Question.NAME)
public class Question extends BaseGameObject {
  public static final String NAME = "Question";

  @Key
  @DataProperty("hasQuestionDescription")
  private String questionDescription;

  public String getQuestionDescription() {
    return questionDescription;
  }

  public void setQuestionDescription(String questionDescription) {
    this.questionDescription = questionDescription;
  }

}
