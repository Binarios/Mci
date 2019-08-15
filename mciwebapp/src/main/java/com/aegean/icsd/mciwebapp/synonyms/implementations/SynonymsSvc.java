package com.aegean.icsd.mciwebapp.synonyms.implementations;

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
import com.aegean.icsd.mciwebapp.synonyms.beans.Synonyms;
import com.aegean.icsd.mciwebapp.synonyms.beans.SynonymResponse;
import com.aegean.icsd.mciwebapp.synonyms.dao.ISynonymsDao;
import com.aegean.icsd.mciwebapp.synonyms.interfaces.ISynonymsSvc;

import com.sun.istack.Nullable;

@Service
public class SynonymsSvc implements ISynonymsSvc {
  private static Logger LOGGER = Logger.getLogger(SynonymsSvc.class);

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private ISynonymsDao dao;

  @Override
  public List<SynonymResponse> getGames(String playerName) throws MciException {
    if (StringUtils.isEmpty(playerName)) {
      throw GameExceptions.InvalidRequest(Synonyms.NAME);
    }

    List<Synonyms> synonyms;
    try {
      synonyms = generator.getGamesForPlayer(Synonyms.NAME, playerName, Synonyms.class);
    } catch (EngineException e) {
      throw GameExceptions.FailedToRetrieveGames(Synonyms.NAME, playerName, e);
    }

    List<SynonymResponse> res = new ArrayList<>();
    for (Synonyms synonym : synonyms) {
      res.add(toResponse(synonym, null, null));
    }
    return res;
  }

  @Override
  public SynonymResponse createGame(String playerName, Difficulty difficulty) throws MciException {
    LOGGER.info(String.format("Creating Synonym game for player %s at the difficulty %s",
      playerName, difficulty.name()));

    if (StringUtils.isEmpty(playerName)) {
      throw GameExceptions.InvalidRequest(Synonyms.NAME);
    }

    String fullName = Utils.getFullGameName(Synonyms.NAME, difficulty);
    int lastCompletedLevel;
    try {
      lastCompletedLevel = generator.getLastCompletedLevel(fullName, difficulty, playerName);
    } catch (EngineException e) {
      throw GameExceptions.FailedToRetrieveLastLevel(Synonyms.NAME, difficulty, playerName, e);
    }
    int newLevel = lastCompletedLevel + 1;

    EntityRestriction maxCompleteTimeRes;
    try {
      maxCompleteTimeRes = rules.getEntityRestriction(fullName, "maxCompletionTime");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Synonyms.NAME, e);
    }

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

    Synonyms toCreate = new Synonyms();
    toCreate.setMaxCompletionTime(Long.parseLong("" + generator.generateIntDataValue(maxCompleteTimeRes.getDataRange())));
    toCreate.setPlayerName(playerName);
    toCreate.setLevel(newLevel);
    toCreate.setDifficulty(difficulty);
    try {
      generator.upsertGame(toCreate);
    } catch (EngineException e) {
      throw GameExceptions.GenerationError(Synonyms.NAME, e);
    }

    Word criteria = new Word();
    criteria.setSynonym(true);

    List<Word> words;
    try {
      words = wordProvider.getNewWordsFor(fullName, hasWordRes.getCardinality(), criteria);
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Synonyms.NAME, e);
    }

    if (words.isEmpty()) {
      throw GameExceptions.GenerationError(Synonyms.NAME, "No words are available for this level");
    }

    Collections.shuffle(words, new Random(System.currentTimeMillis()));
    Word mainWord = words.remove(0);

    List<Word> relatedWords;
    try {
      relatedWords = wordProvider.selectWordsByEntityId(mainWord.getId());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Synonyms.NAME, e);
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
        throw GameExceptions.GenerationError(Synonyms.NAME, e);
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
      throw GameExceptions.GenerationError(Synonyms.NAME, e);
    }

    return toResponse(toCreate, mainWord, words);
  }

  @Override
  public SynonymResponse getGame(String id, String player) throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)) {
      throw GameExceptions.InvalidRequest(Synonyms.NAME);
    }

    Synonyms synonyms;
    try {
      synonyms = generator.getGameWithId(id, player, Synonyms.class);
    } catch (EngineException e) {
      throw GameExceptions.UnableToRetrieveGame(Synonyms.NAME, id, player, e);
    }

    List<Word> words;
    try{
      words = wordProvider.selectWordsByEntityId(synonyms.getId());
    } catch (ProviderException e) {
      throw GameExceptions.FailedToRetrieveWord(Synonyms.NAME, synonyms.getId(), e);
    }

    Word mainWord;
    try {
      String wordNode = dao.getMainWord(synonyms.getId());
      mainWord = wordProvider.selectWordByNode(wordNode);
    } catch (ProviderException e) {
      throw GameExceptions.FailedToRetrieveWord(Synonyms.NAME, synonyms.getId(), e);
    }
    removeWordFromList(mainWord, words);
    return toResponse(synonyms, mainWord, words);
  }

  @Override
  public SynonymResponse solveGame(String id, String player, Long completionTime, String solution) throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)
      || completionTime == null
      || solution.isEmpty()) {
      throw GameExceptions.InvalidRequest(Synonyms.NAME);
    }

    Synonyms synonyms;
    try {
      synonyms = generator.getGameWithId(id, player, Synonyms.class);
    } catch (EngineException e) {
      throw GameExceptions.UnableToRetrieveGame(Synonyms.NAME, id, player, e);
    }

    if (completionTime > synonyms.getMaxCompletionTime()) {
      throw GameExceptions.SurpassedMaxCompletionTime(Synonyms.NAME, id, synonyms.getMaxCompletionTime());
    }
    if (!StringUtils.isEmpty(synonyms.getCompletedDate())) {
      throw GameExceptions.GameIsAlreadySolvedAt(Synonyms.NAME, id, synonyms.getCompletedDate());
    }

    boolean areSynonyms;
    Word mainWord;
    List<Word> words;
    try {
      Word solutionWord = wordProvider.getWordWithValue(solution);
      words = wordProvider.selectWordsByEntityId(synonyms.getId());
      Word found = words.stream()
        .filter(x -> x.getId().equals(solutionWord.getId()))
        .findFirst()
        .orElse(null);
      if (found == null) {
        throw GameExceptions.UnableToSolve(Synonyms.NAME, "Provided word is not associated with this game");
      }

      String mainWordId = dao.getMainWord(synonyms.getId());
      mainWord = wordProvider.selectWordByNode(mainWordId);
      if (mainWord.getId() == null) {
        throw GameExceptions.FailedToRetrieveWord(Synonyms.NAME, mainWordId);
      }

      areSynonyms = wordProvider.areSynonyms(mainWord, solutionWord);

    } catch (ProviderException e) {
      throw GameExceptions.UnableToSolve(Synonyms.NAME, e);
    }

    if (areSynonyms) {
      synonyms.setCompletionTime(completionTime);
      synonyms.setCompletedDate(String.valueOf(System.currentTimeMillis()));
      try {
        generator.upsertGame(synonyms);
      } catch (EngineException e) {
        throw  GameExceptions.GenerationError(Synonyms.NAME, e);
      }
    }

    removeWordFromList(mainWord, words);
    return toResponse(synonyms, mainWord, words);
  }

  void removeWordFromList(Word toRemove, List<Word> words) {
    Word main = words.stream()
      .filter(x -> x.getId().equals(toRemove.getId()))
      .findFirst()
      .get();

    words.remove(main);
  }

  SynonymResponse toResponse(Synonyms synonyms, @Nullable Word mainWord, @Nullable List<Word> words) {
    SynonymResponse response = new SynonymResponse(synonyms);
    response.setSolved(synonyms.getCompletedDate() != null);
    if (mainWord != null) {
      response.setWord(mainWord.getValue());
    }
    if (words != null) {
      response.setChoices(words.stream().map(Word::getValue).collect(Collectors.toList()));
    }
    return response;
  }
}
