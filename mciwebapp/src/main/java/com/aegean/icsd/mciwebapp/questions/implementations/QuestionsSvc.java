package com.aegean.icsd.mciwebapp.questions.implementations;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.questions.beans.Question;
import com.aegean.icsd.mciobjects.questions.beans.QuestionData;
import com.aegean.icsd.mciobjects.questions.interfaces.IQuestionProvider;
import com.aegean.icsd.mciobjects.words.beans.Word;
import com.aegean.icsd.mciobjects.words.interfaces.IWordProvider;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.questions.beans.Questions;
import com.aegean.icsd.mciwebapp.questions.beans.QuestionsResponse;
import com.aegean.icsd.mciwebapp.questions.interfaces.IQuestionsSvc;

@Service
public class QuestionsSvc extends AbstractGameSvc<Questions, QuestionsResponse> implements IQuestionsSvc {

  @Autowired
  private IRules rules;

  @Autowired
  private IQuestionProvider questionProvider;

  @Autowired
  private IWordProvider wordProvider;

  @Override
  protected void handleDataTypeRestrictions(String fullName, Questions toCreate) throws MciException {
    //no datatype restrictions
  }

  @Override
  protected void handleObjectRestrictions(String fullName, Questions toCreate) throws MciException {
    EntityRestriction hasQuestion;
    EntityRestriction hasAnswer;
    EntityRestriction hasChoice;
    EntityRestriction hasCategory;
    try {
      hasQuestion = rules.getEntityRestriction(Questions.NAME, "hasQuestion");
      hasAnswer = rules.getEntityRestriction(Questions.NAME, "hasAnswer");
      hasChoice = rules.getEntityRestriction(Questions.NAME, "hasChoice");
      hasCategory = rules.getEntityRestriction(Questions.NAME, "hasCategory");
    } catch (RulesException e) {
      throw GameExceptions.UnableToResponse(Questions.NAME, e);
    }

    List<QuestionData> questionsData;
    try {
      questionsData = questionProvider.getNewQuestionsFor(fullName, hasQuestion.getCardinality(), false);
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Questions.NAME,e);
    }

    for (QuestionData data : questionsData) {

      if (data.getChoices().size() > hasChoice.getCardinality()) {
        data.getChoices().subList(hasChoice.getCardinality(), data.getChoices().size()).clear();
      }

      createObjRelation(toCreate, data.getQuestion(), hasQuestion.getOnProperty());
      createObjRelation(toCreate, data.getAnswer(), hasAnswer.getOnProperty());
      createObjRelation(toCreate, data.getCategory(), hasCategory.getOnProperty());
      createObjRelation(toCreate, data.getChoices(), hasChoice.getOnProperty());
    }
  }

  @Override
  protected boolean isValid(Object solution) {
    return solution != null && !StringUtils.isEmpty(solution.toString());
  }

  @Override
  protected boolean checkSolution(Questions game, Object solution) throws MciException {
    EntityRestriction hasAnswer;
    try {
      hasAnswer = rules.getEntityRestriction(Questions.NAME, "hasAnswer");
    } catch (RulesException e) {
      throw GameExceptions.UnableToResponse(Questions.NAME, e);
    }

    List<Word> answerResults;
    try {
      answerResults = wordProvider.selectWordsByEntityIdOnProperty(game.getId(),hasAnswer.getOnProperty());
    } catch (ProviderException e) {
      throw GameExceptions.UnableToResponse(Questions.NAME, e);
    }

    Word found = answerResults.stream()
      .filter(x -> x.getValue().equals(solution.toString()))
      .findFirst()
      .orElse(null);

    return found != null;
  }

  @Override
  protected QuestionsResponse toResponse(Questions game) throws MciException {
    EntityRestriction hasAnswer;
    EntityRestriction hasChoice;
    EntityRestriction hasCategory;
    try {
      hasAnswer = rules.getEntityRestriction(Questions.NAME, "hasAnswer");
      hasChoice = rules.getEntityRestriction(Questions.NAME, "hasChoice");
      hasCategory = rules.getEntityRestriction(Questions.NAME, "hasCategory");
    } catch (RulesException e) {
      throw GameExceptions.UnableToResponse(Questions.NAME, e);
    }

    List<Question> qResults;
    List<Word> answerResults;
    List<Word> choiceResults;
    List<Word> categoryResults;
    try {
      qResults = questionProvider.selectQuestionsForEntityId(game.getId());
      answerResults = wordProvider.selectWordsByEntityIdOnProperty(game.getId(),hasAnswer.getOnProperty());
      choiceResults = wordProvider.selectWordsByEntityIdOnProperty(game.getId(),hasChoice.getOnProperty());
      categoryResults = wordProvider.selectWordsByEntityIdOnProperty(game.getId(),hasCategory.getOnProperty());
    } catch (ProviderException e) {
      throw GameExceptions.UnableToResponse(Questions.NAME, e);
    }

    if (qResults.isEmpty() || answerResults.isEmpty() || choiceResults.isEmpty() || categoryResults.isEmpty()) {
      throw GameExceptions.UnableToResponse(Questions.NAME, "Could not retrieve some information " +
        "regarding game with id %s " + game.getId());
    }

    String question = qResults.get(0).getDescription();
    String category = categoryResults.get(0).getValue();
    String answer = answerResults.get(0).getValue();
    List<String> choices = choiceResults.stream().map(Word::getValue).collect(Collectors.toList());
    choices.add(answer);

    QuestionsResponse response = new QuestionsResponse(game);
    response.setCategory(category);
    response.setChoices(choices);
    response.setQuestion(question);
    return response;
  }
}
