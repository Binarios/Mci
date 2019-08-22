package com.aegean.icsd.mciobjects.questions.beans;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;

@Entity(Question.NAME)
public class Question extends BaseGameObject {
  public static final String NAME = "Question";

  @DataProperty("hasDifficulty")
  private Difficulty difficulty;

  @DataProperty("hasQuestionDescription")
  private String description;

  @DataProperty("isImageQuestion")
  private Boolean imageQuestion;

  @DataProperty("isTextQuestion")
  private Boolean textQuestion;
}
