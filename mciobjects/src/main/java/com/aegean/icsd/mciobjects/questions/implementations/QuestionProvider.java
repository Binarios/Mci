package com.aegean.icsd.mciobjects.questions.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.implementations.Generator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.common.daos.IObjectsDao;
import com.aegean.icsd.mciobjects.common.implementations.ProviderExceptions;
import com.aegean.icsd.mciobjects.common.interfaces.IObjectFileProvider;
import com.aegean.icsd.mciobjects.images.beans.Image;
import com.aegean.icsd.mciobjects.images.interfaces.IImageProvider;
import com.aegean.icsd.mciobjects.questions.beans.Question;
import com.aegean.icsd.mciobjects.questions.beans.QuestionData;
import com.aegean.icsd.mciobjects.questions.configurations.QuestionConfiguration;
import com.aegean.icsd.mciobjects.questions.interfaces.IQuestionProvider;
import com.aegean.icsd.mciobjects.words.beans.Word;
import com.aegean.icsd.mciobjects.words.interfaces.IWordProvider;

@Service
public class QuestionProvider implements IQuestionProvider {

  @Autowired
  private Map<String, EntityRestriction> questionRules;

  @Autowired
  private IObjectFileProvider fileProvider;

  @Autowired
  private IImageProvider imageProvider;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private QuestionConfiguration questionConfig;

  @Autowired
  private Generator generator;

  @Autowired
  private IObjectsDao dao;

  @Override
  public List<QuestionData> getNewQuestionsFor(String entityName, int count, boolean isImageQuestion) throws ProviderException {
    EntityRestriction hasCategoryRes = questionRules.get("hasCategory");
    List<QuestionData> result = new ArrayList<>();

    List<String> lines = fileProvider.getLines(questionConfig.getLocation() + "/" + questionConfig.getFilename());
    for (String line : lines) {
      String[] fragments = line.split(questionConfig.getDelimiter());
      String questionRaw = fragments[questionConfig.getQuestionIndex()];
      String answer = fragments[questionConfig.getCorrectAnswerIndex()];
      String choicesConcat = fragments[questionConfig.getChoicesIndex()];
      String[] choices = choicesConcat.split(questionConfig.getChoicesDelimiter());
      String category = fragments[questionConfig.getCategoryIndex()];
      String difficulty = fragments[questionConfig.getDifficultyIndex()];

      try {
        Question question = getNewQuestion(questionRaw, difficulty, isImageQuestion);
        if (question == null) {
          continue;
        }
        Word answerWord = wordProvider.getWordWithValue(answer);
        Word categoryWord = wordProvider.getWordWithValue(category);
        List<Word> choiceWords = new ArrayList<>();
        for (String choice : choices) {
          Word choiceWord = wordProvider.getWordWithValue(choice);
          choiceWords.add(choiceWord);
        }

        generator.createObjRelation(question, categoryWord, hasCategoryRes.getOnProperty());

        QuestionData data = new QuestionData();
        data.setAnswer(answerWord);
        data.setChoices(choiceWords);
        data.setCategory(categoryWord);
        data.setQuestion(question);

        result.add(data);
        if (result.size() == count) {
          break;
        }

      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(Question.NAME, e);
      }
    }

    if (result.isEmpty()) {
      throw ProviderExceptions.UnableToGetObject(String.format("Couldn't get new questions for " +
        "the entity %s", entityName));
    }
    if (result.size() != count) {
      throw ProviderExceptions.UnableToGetObject(String.format("Couldn't get the requested amount of %s questions " +
        "for the entity %s", count, entityName));
    }

    return result;
  }

  @Override
  public List<Question> selectQuestionsForEntityId(String entityId) throws ProviderException {
    List<String> ids = dao.getAssociatedObjectsOfEntityId(entityId, Question.class);
    List<Question> questions = new ArrayList<>();

    for (String id : ids) {
      Question question = new Question();
      question.setId(id);
      try {
        List<Question> results = generator.selectGameObject(question);
        if (!results.isEmpty()) {
          Question q = results.get(0);
          if (q.isImageQuestion()) {
            List<Image> associatedImages = imageProvider.selectImagesByEntityId(q.getId());
            if (associatedImages.isEmpty()) {
              throw ProviderExceptions.UnableToGetObject(Question.NAME + " for entityId = " + q.getId());
            }
            q.setDescription(associatedImages.get(0).getPath());
          }
          questions.add(q);
        }
      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetObject(Question.NAME + " for entityId = " + entityId, e);
      }
    }
    return questions;
  }


  Question getNewQuestion(String description, String difficulty, boolean isImageQuestion) throws EngineException, ProviderException {
    UrlValidator validator = new UrlValidator();
    boolean isImageDescription = validator.isValid(description);

    if (isImageQuestion != isImageDescription) {
      return null;
    }

    Question question = new Question();
    question.setImageQuestion(isImageDescription);
    question.setTextQuestion(!isImageDescription);
    question.setDifficulty(Difficulty.valueOf(difficulty.toUpperCase(Locale.ENGLISH)));
    if (question.isTextQuestion()) {
      question.setDescription(description);
    }

    List<Question> questions = generator.selectGameObject(question);
    if (questions.isEmpty()) {
      generator.upsertGameObject(question);
      associateWithImage(question, description);
    } else {
      question = null;
    }
    return question;
  }

  void associateWithImage(Question question, String description) throws EngineException {
    EntityRestriction hasImageRes = questionRules.get("hasImage");
    if (question.isImageQuestion()) {
      Image imageDescription = new Image();
      imageDescription.setPath(description);
      List<Image> images = generator.selectGameObject(imageDescription);
      if (images.isEmpty()) {
        generator.upsertGameObject(imageDescription);
      } else {
        imageDescription = images.get(0);
      }
      generator.createObjRelation(question, imageDescription, hasImageRes.getOnProperty());
    }
  }
}
