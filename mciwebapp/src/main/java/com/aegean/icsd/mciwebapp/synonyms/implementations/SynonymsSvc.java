package com.aegean.icsd.mciwebapp.synonyms.implementations;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;
import com.aegean.icsd.mciwebapp.synonyms.beans.SynonymResponse;
import com.aegean.icsd.mciwebapp.synonyms.beans.Synonyms;
import com.aegean.icsd.mciwebapp.synonyms.dao.ISynonymsDao;
import com.aegean.icsd.mciwebapp.synonyms.interfaces.ISynonymsSvc;

@Service
public class SynonymsSvc extends AbstractGameSvc<Synonyms, SynonymResponse> implements ISynonymsSvc {
  private static Logger LOGGER = Logger.getLogger(SynonymsSvc.class);

  @Autowired
  private IRules rules;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private ISynonymsDao dao;

  @Autowired
  private IGenerator generator;

  @Override
  protected boolean isValid(Object solution) {
    return !StringUtils.isEmpty(solution.toString());
  }

  @Override
  protected boolean checkSolution(Synonyms game, Object solution) throws MciException {
    boolean areSynonyms;
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
        throw GameExceptions.UnableToSolve(Synonyms.NAME, "Provided word is not associated with this game");
      }

      String mainWordId = dao.getMainWord(game.getId());
      mainWord = wordProvider.selectWordByNode(mainWordId);
      if (mainWord.getId() == null) {
        throw GameExceptions.FailedToRetrieveWord(Synonyms.NAME, mainWordId);
      }

      areSynonyms = wordProvider.areSynonyms(mainWord, solutionWord);
      return areSynonyms;
    } catch (ProviderException e) {
      throw GameExceptions.UnableToSolve(Synonyms.NAME, e);
    }
  }

  @Override
  protected void handleRestrictions(String fullName, Synonyms toCreate) throws MciException {

    EntityRestriction hasMainWordRes;
    try {
      hasMainWordRes = rules.getEntityRestriction(fullName, "hasMainWord");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Synonyms.NAME, e);
    }

    EntityRestriction hasWordRes;
    try {
      hasWordRes = rules.getEntityRestriction(fullName, "hasWord");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Synonyms.NAME, e);
    }

    Word criteria = new Word();
    criteria.setSynonym(true);

    Word mainWord;
    try {
      List<Word> mainWords = wordProvider.getNewWordsFor(fullName, hasMainWordRes.getCardinality(), criteria);
      mainWord = mainWords.get(hasMainWordRes.getCardinality() - 1);
      createObjRelation(toCreate, mainWords, hasMainWordRes.getOnProperty());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Synonyms.NAME, e);
    }

    List<Word> words;
    try {
      words = wordProvider.getNewWordsFor(fullName, hasWordRes.getCardinality(), criteria);
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Synonyms.NAME, e);
    }

    if (words.isEmpty()) {
      throw GameExceptions.GenerationError(Synonyms.NAME, "No words are available for this level");
    }

    List<Word> relatedWords;
    try {
      relatedWords = wordProvider.selectWordsByEntityId(mainWord.getId());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Synonyms.NAME, e);
    }

    words.removeIf(x -> x.getId().equals(mainWord.getId()));
    List<Word> existing = words.stream()
      .filter(x -> {
        List<Word> found = relatedWords.stream()
          .filter(y -> y.getId().equals(x.getId()))
          .collect(Collectors.toList());
        return x.isSynonym() != null && x.isSynonym() && !found.isEmpty();
      })
      .collect(Collectors.toList());

    if (!existing.isEmpty()) {
      createObjRelation(toCreate, words, hasWordRes.getOnProperty());
    } else {
      Collections.shuffle(relatedWords, new Random(System.currentTimeMillis()));
      Collections.shuffle(words, new Random(System.currentTimeMillis()));
      Word relatedWord = relatedWords.get(0);
      words.remove(0);
      words.add(relatedWord);
      createObjRelation(toCreate, words, hasWordRes.getOnProperty());
    }
  }

  @Override
  protected SynonymResponse toResponse(Synonyms toCreate) throws MciException {
    List<Word> words;
    Word mainWord;
    try {
      String mainWordId = dao.getMainWord(toCreate.getId());
      mainWord = wordProvider.selectWordByNode(mainWordId);
      words = wordProvider.selectWordsByEntityId(toCreate.getId());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Synonyms.NAME, e);
    }
    SynonymResponse response = new SynonymResponse(toCreate);
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

  @Override
  protected void handleDataTypeRestrictions(String fullName, Synonyms toCreate) throws MciException {
    return;
  }
  void removeWordFromList(Word toRemove, List<Word> words) {
    Word main = words.stream()
      .filter(x -> x.getId().equals(toRemove.getId()))
      .findFirst()
      .get();

    words.remove(main);
  }
}
