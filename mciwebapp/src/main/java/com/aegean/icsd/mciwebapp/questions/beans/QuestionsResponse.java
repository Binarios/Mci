package com.aegean.icsd.mciwebapp.questions.beans;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class QuestionsResponse extends ServiceResponse<Questions> {

  public QuestionsResponse(Questions game) {
    super(game);
  }
}
