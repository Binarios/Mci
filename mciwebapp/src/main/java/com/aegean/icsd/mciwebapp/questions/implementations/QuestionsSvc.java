package com.aegean.icsd.mciwebapp.questions.implementations;

import org.springframework.stereotype.Service;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.questions.beans.Questions;
import com.aegean.icsd.mciwebapp.questions.beans.QuestionsResponse;
import com.aegean.icsd.mciwebapp.questions.interfaces.IQuestionsSvc;

@Service
public class QuestionsSvc extends AbstractGameSvc<Questions, QuestionsResponse> implements IQuestionsSvc {

  @Override
  protected void handleDataTypeRestrictions(String fullName, Questions toCreate) throws MciException {

  }

  @Override
  protected void handleObjectRestrictions(String fullName, Questions toCreate) throws MciException {

  }

  @Override
  protected boolean isValid(Object solution) {
    return false;
  }

  @Override
  protected boolean checkSolution(Questions game, Object solution) throws MciException {
    return false;
  }

  @Override
  protected QuestionsResponse toResponse(Questions toCreate) throws MciException {
    return null;
  }
}
