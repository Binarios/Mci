package com.aegean.icsd.mciobjects.questions.interfaces;

import java.util.List;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.questions.beans.Question;
import com.aegean.icsd.mciobjects.questions.beans.QuestionData;

public interface IQuestionProvider {

  List<QuestionData> getNewQuestionsFor(String entityName, int count, boolean isImageQuestion) throws ProviderException;
  List<Question> selectQuestionsForEntityId(String entityId) throws ProviderException;
}
