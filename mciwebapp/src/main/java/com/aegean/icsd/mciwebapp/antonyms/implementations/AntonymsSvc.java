package com.aegean.icsd.mciwebapp.antonyms.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.antonyms.beans.AntonymResponse;
import com.aegean.icsd.mciwebapp.antonyms.beans.Antonyms;
import com.aegean.icsd.mciwebapp.antonyms.dao.IAntonymsDao;
import com.aegean.icsd.mciwebapp.antonyms.interfaces.IAntonymsSvc;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;

@Service
public class AntonymsSvc extends AbstractGameSvc<Antonyms, AntonymResponse> implements IAntonymsSvc {
  private static Logger LOGGER = Logger.getLogger(AntonymsSvc.class);

  @Autowired
  private IRules rules;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private IAntonymsDao dao;

  @Override
  protected boolean isValid(Object solution) {
    return !StringUtils.isEmpty(solution.toString());
  }

  @Override
  protected boolean checkSolution(Antonyms game, Object solution) throws MciException {
    boolean areAntonyms;
    Word mainWord;
    List<Word> words;
    try {
      Word solutionWord = wordProvider.getWordWithValue((String)solution);
      words = wordProvider.selectWordsByEntityId(game.getId());
      Word found = words.stream()
        .filter(x -> x.getId().equals(solutionWord.getId()))
        .findFirst()
        .orElse(null);
      if (found == null) {
        throw GameExceptions.UnableToSolve(Antonyms.NAME, "Provided word is not associated with this game");
      }

      String mainWordId = dao.getMainWord(game.getId());
      mainWord = wordProvider.selectWordByNode(mainWordId);
      if (mainWord.getId() == null) {
        throw GameExceptions.FailedToRetrieveWord(Antonyms.NAME, mainWordId);
      }

      areAntonyms = wordProvider.areAntonyms(mainWord, solutionWord);
      return areAntonyms;
    } catch (ProviderException e) {
      throw GameExceptions.UnableToSolve(Antonyms.NAME, e);
    }
  }

  @Override
  protected Map<EntityRestriction, List<BaseGameObject>> getRestrictions(String fullName, Antonyms toCreate) throws MciException {
    Map<EntityRestriction, List<BaseGameObject>> restrictions = new HashMap<>();
    EntityRestriction hasMainWordRes;
    try {
      hasMainWordRes = rules.getEntityRestriction(fullName, "hasMainWord");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Antonyms.NAME, e);
    }

    EntityRestriction hasWordRes;
    try {
      hasWordRes = rules.getEntityRestriction(fullName, "hasWord");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Antonyms.NAME, e);
    }

    Word criteria = new Word();
    criteria.setAntonym(true);

    List<Word> words;
    try {
      words = wordProvider.getNewWordsFor(fullName, hasWordRes.getCardinality(), criteria);
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Antonyms.NAME, e);
    }

    if (words.isEmpty()) {
      throw GameExceptions.GenerationError(Antonyms.NAME, "No words are available for this level");
    }

    Collections.shuffle(words, new Random(System.currentTimeMillis()));
    Word mainWord = words.remove(0);

    List<BaseGameObject> hasMainWordResObjs = new ArrayList<>();
    hasMainWordResObjs.add(mainWord);
    restrictions.put(hasMainWordRes, hasMainWordResObjs);

    List<Word> relatedWords;
    try {
      relatedWords = wordProvider.selectWordsByEntityId(mainWord.getId());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Antonyms.NAME, e);
    }

    Word antonym = relatedWords.stream()
      .filter(x -> x.isAntonym() != null && x.isAntonym())
      .filter(x -> {
        Word found = words.stream()
          .filter(y -> y.getId().equals(x.getId()))
          .findFirst()
          .orElse(null);
        return found == null;
      })
      .findFirst()
      .orElse(null);

    if (antonym == null) {
      //means already exists in the word list. In that case we just get a new word.
      try {
        List<Word> existing = words.stream()
          .filter(x->{
            Word found = relatedWords.stream()
              .filter(y -> y.getId().equals(x.getId()))
              .findFirst()
              .orElse(null);
            return found != null;
          })
          .collect(Collectors.toList());
        words.removeAll(existing);
        int nb = hasWordRes.getCardinality() - words.size();
        List<Word> newWords = wordProvider.getNewWordsFor(fullName, nb, criteria);
        words.addAll(newWords);
      } catch (ProviderException e) {
        throw GameExceptions.GenerationError(Antonyms.NAME, e);
      }
    }

    List<BaseGameObject> hasWordResObjs = new ArrayList<>();
    hasWordResObjs.addAll(words);
    restrictions.put(hasMainWordRes, hasWordResObjs);

    return restrictions;
  }

  @Override
  protected AntonymResponse toResponse(Antonyms toCreate) throws MciException {
    List<Word> words;
    Word mainWord;
    try {
      String mainWordId = dao.getMainWord(toCreate.getId());
      mainWord = wordProvider.selectWordByNode(mainWordId);
      words = wordProvider.selectWordsByEntityId(toCreate.getId());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Antonyms.NAME, e);
    }
    AntonymResponse response = new AntonymResponse(toCreate);
    response.setSolved(toCreate.getCompletedDate() != null);
    if (mainWord != null) {
      response.setWord(mainWord.getValue());
    }
    if (words != null) {
      removeWordFromList(mainWord, words);
      response.setChoices(words.stream().map(Word::getValue).collect(Collectors.toList()));
    }
    return response;
  }

  void removeWordFromList(Word toRemove, List<Word> words) {
    Word main = words.stream()
      .filter(x -> x.getId().equals(toRemove.getId()))
      .findFirst()
      .get();

    words.remove(main);
  }
}
