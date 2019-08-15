package com.aegean.icsd.mciwebapp.synonym.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;
import com.aegean.icsd.mciwebapp.synonym.beans.Synonym;
import com.aegean.icsd.mciwebapp.synonym.beans.SynonymResponse;
import com.aegean.icsd.mciwebapp.synonym.dao.ISynonymDao;
import com.aegean.icsd.mciwebapp.synonym.interfaces.ISynonymsSvc;

import com.sun.istack.Nullable;

@Service
public class SynonymSvc implements ISynonymsSvc {
  private static Logger LOGGER = Logger.getLogger(SynonymSvc.class);

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private ISynonymDao dao;

  @Override
  public List<SynonymResponse> getGames(String playerName) throws MciException {
    if (StringUtils.isEmpty(playerName)) {
      throw GameExceptions.InvalidRequest(Synonym.NAME);
    }

    List<Synonym> synonyms;
    try {
      synonyms = generator.getGamesForPlayer(Synonym.NAME, playerName, Synonym.class);
    } catch (EngineException e) {
      throw GameExceptions.FailedToRetrieveGames(Synonym.NAME, playerName, e);
    }

    List<SynonymResponse> res = new ArrayList<>();
    for (Synonym synonym : synonyms) {
      res.add(toResponse(synonym, null, null));
    }
    return res;
  }

  @Override
  public SynonymResponse createGame(String playerName, Difficulty difficulty) throws MciException {
    LOGGER.info(String.format("Creating Synonym game for player %s at the difficulty %s",
      playerName, difficulty.name()));

    if (StringUtils.isEmpty(playerName)) {
      throw GameExceptions.InvalidRequest(Synonym.NAME);
    }

    String fullName = Utils.getFullGameName(Synonym.NAME, difficulty);
    int lastCompletedLevel;
    try {
      lastCompletedLevel = generator.getLastCompletedLevel(fullName, difficulty, playerName);
    } catch (EngineException e) {
      throw GameExceptions.FailedToRetrieveLastLevel(Synonym.NAME, difficulty, playerName, e);
    }
    int newLevel = lastCompletedLevel + 1;

    EntityRestriction maxCompleteTimeRes;
    try {
      maxCompleteTimeRes = rules.getEntityRestriction(fullName, "maxCompletionTime");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Synonym.NAME, e);
    }

    EntityRestriction hasMainWordRes;
    try {
      hasMainWordRes = rules.getEntityRestriction(fullName, "hasMainWord");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Synonym.NAME, e);
    }

    EntityRestriction hasWordRes;
    try {
      hasWordRes = rules.getEntityRestriction(fullName, "hasWord");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Synonym.NAME, e);
    }

    Synonym toCreate = new Synonym();
    toCreate.setMaxCompletionTime(Long.parseLong("" + generator.generateIntDataValue(maxCompleteTimeRes.getDataRange())));
    toCreate.setPlayerName(playerName);
    toCreate.setLevel(newLevel);
    toCreate.setDifficulty(difficulty);
    try {
      generator.upsertGame(toCreate);
    } catch (EngineException e) {
      throw GameExceptions.GenerationError(Synonym.NAME, e);
    }

    Word criteria = new Word();
    criteria.setSynonym(true);

    List<Word> words;
    try {
      words = wordProvider.getNewWordsFor(fullName, hasWordRes.getCardinality(), criteria);
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Synonym.NAME, e);
    }

    if (words.isEmpty()) {
      throw GameExceptions.GenerationError(Synonym.NAME, "No words are available for this level");
    }

    Collections.shuffle(words, new Random(System.currentTimeMillis()));
    Word mainWord = words.remove(0);

    List<Word> relatedWords;
    try {
      relatedWords = wordProvider.selectWordsByEntityId(mainWord.getId());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Synonym.NAME, e);
    }

    Word synonym = relatedWords.stream()
      .filter(x -> x.isSynonym() != null && x.isSynonym())
      .filter(x -> {
        Word found = words.stream()
          .filter(y -> y.getId().equals(x.getId()))
          .findFirst()
          .orElse(null);
        return found == null;
      })
      .findFirst()
      .orElse(null);

    if (synonym == null) {
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
        throw GameExceptions.GenerationError(Synonym.NAME, e);
      }
    }

    words.add(synonym);
    Collections.shuffle(words, new Random(System.currentTimeMillis()));
    try {
      generator.createObjRelation(toCreate.getId(), hasMainWordRes.getOnProperty(), mainWord.getId());
      for (Word word : words) {
        generator.createObjRelation(toCreate.getId(), hasWordRes.getOnProperty(), word.getId());
      }
    } catch (EngineException e) {
      throw GameExceptions.GenerationError(Synonym.NAME, e);
    }

    return toResponse(toCreate, mainWord, words);
  }

  @Override
  public SynonymResponse getGame(String id, String player) throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)) {
      throw GameExceptions.InvalidRequest(Synonym.NAME);
    }

    Synonym synonym;
    try {
      synonym = generator.getGameWithId(id, player, Synonym.class);
    } catch (EngineException e) {
      throw GameExceptions.UnableToRetrieveGame(Synonym.NAME, id, player, e);
    }

    List<Word> words;
    try{
      words = wordProvider.selectWordsByEntityId(synonym.getId());
    } catch (ProviderException e) {
      throw GameExceptions.FailedToRetrieveWord(Synonym.NAME, synonym.getId(), e);
    }

    Word mainWord;
    try {
      String wordNode = dao.getMainWord(synonym.getId());
      mainWord = wordProvider.selectWordByNode(wordNode);
    } catch (ProviderException e) {
      throw GameExceptions.FailedToRetrieveWord(Synonym.NAME, synonym.getId(), e);
    }
    removeWordFromList(mainWord, words);
    return toResponse(synonym, mainWord, words);
  }

  @Override
  public SynonymResponse solveGame(String id, String player, Long completionTime, String solution) throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)
      || completionTime == null
      || solution.isEmpty()) {
      throw GameExceptions.InvalidRequest(Synonym.NAME);
    }

    Synonym synonym;
    try {
      synonym = generator.getGameWithId(id, player, Synonym.class);
    } catch (EngineException e) {
      throw GameExceptions.UnableToRetrieveGame(Synonym.NAME, id, player, e);
    }

    if (completionTime > synonym.getMaxCompletionTime()) {
      throw GameExceptions.SurpassedMaxCompletionTime(Synonym.NAME, id, synonym.getMaxCompletionTime());
    }
    if (!StringUtils.isEmpty(synonym.getCompletedDate())) {
      throw GameExceptions.GameIsAlreadySolvedAt(Synonym.NAME, id, synonym.getCompletedDate());
    }

    boolean areSynonyms;
    Word mainWord;
    List<Word> words;
    try {
      Word solutionWord = wordProvider.getWordWithValue(solution);
      words = wordProvider.selectWordsByEntityId(synonym.getId());
      Word found = words.stream()
        .filter(x -> x.getId().equals(solutionWord.getId()))
        .findFirst()
        .orElse(null);
      if (found == null) {
        throw GameExceptions.UnableToSolve(Synonym.NAME, "Provided word is not associated with this game");
      }

      String mainWordId = dao.getMainWord(synonym.getId());
      mainWord = wordProvider.selectWordByNode(mainWordId);
      if (mainWord.getId() == null) {
        throw GameExceptions.FailedToRetrieveWord(Synonym.NAME, mainWordId);
      }

      areSynonyms = wordProvider.areSynonyms(mainWord, solutionWord);

    } catch (ProviderException e) {
      throw GameExceptions.UnableToSolve(Synonym.NAME, e);
    }

    if (areSynonyms) {
      synonym.setCompletionTime(completionTime);
      synonym.setCompletedDate(String.valueOf(System.currentTimeMillis()));
      try {
        generator.upsertGame(synonym);
      } catch (EngineException e) {
        throw  GameExceptions.GenerationError(Synonym.NAME, e);
      }
    }

    removeWordFromList(mainWord, words);
    return toResponse(synonym, mainWord, words);
  }

  void removeWordFromList(Word toRemove, List<Word> words) {
    Word main = words.stream()
      .filter(x -> x.getId().equals(toRemove.getId()))
      .findFirst()
      .get();

    words.remove(main);
  }

  SynonymResponse toResponse(Synonym synonym, @Nullable Word mainWord, @Nullable List<Word> words) {
    SynonymResponse response = new SynonymResponse(synonym);
    response.setSolved(synonym.getCompletedDate() != null);
    if (mainWord != null) {
      response.setWord(mainWord.getValue());
    }
    if (words != null) {
      response.setChoices(words.stream().map(Word::getValue).collect(Collectors.toList()));
    }
    return response;
  }
}
