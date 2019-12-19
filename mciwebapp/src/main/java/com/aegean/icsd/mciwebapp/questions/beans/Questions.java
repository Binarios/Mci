package com.aegean.icsd.mciwebapp.questions.beans;

import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.common.beans.BaseGame;

@Entity(Questions.NAME)
public class Questions extends BaseGame {
  public static final String NAME = "Questions";
}
