package com.aegean.icsd.mciwebapp.antonyms.implementations;

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
import com.aegean.icsd.mciwebapp.antonyms.beans.AntonymResponse;
import com.aegean.icsd.mciwebapp.antonyms.beans.Antonyms;
import com.aegean.icsd.mciwebapp.antonyms.dao.IAntonymsDao;
import com.aegean.icsd.mciwebapp.antonyms.interfaces.IAntonymsSvc;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;
import com.aegean.icsd.mciwebapp.synonyms.beans.SynonymResponse;
import com.aegean.icsd.mciwebapp.synonyms.beans.Synonyms;
import com.aegean.icsd.mciwebapp.synonyms.dao.ISynonymsDao;
import com.aegean.icsd.mciwebapp.synonyms.interfaces.ISynonymsSvc;

import com.sun.istack.Nullable;

@Service
public class AntonymsSvc implements IAntonymsSvc {
  private static Logger LOGGER = Logger.getLogger(AntonymsSvc.class);

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private IAntonymsDao dao;

  @Override
  public List<AntonymResponse> getGames(String playerName) throws MciException {
    if (StringUtils.isEmpty(playerName)) {
      throw GameExceptions.InvalidRequest(Antonyms.NAME);
    }

    List<Antonyms> antonyms;
    try {
      antonyms = generator.getGamesForPlayer(Antonyms.NAME, playerName, Antonyms.class);
    } catch (EngineException e) {
      throw GameExceptions.FailedToRetrieveGames(Antonyms.NAME, playerName, e);
    }

    List<AntonymResponse> res = new ArrayList<>();
    for (Antonyms antonym : antonyms) {
      res.add(toResponse(antonym, null, null));
    }
    return res;
  }

  @Override
  public AntonymResponse createGame(String playerName, Difficulty difficulty) throws MciException {
    LOGGER.info(String.format("Creating Synonym game for player %s at the difficulty %s",
      playerName, difficulty.name()));

    if (StringUtils.isEmpty(playerName)) {
      throw GameExceptions.InvalidRequest(Antonyms.NAME);
    }

    String fullName = Utils.getFullGameName(Antonyms.NAME, difficulty);
    int lastCompletedLevel;
    try {
      lastCompletedLevel = generator.getLastCompletedLevel(fullName, difficulty, playerName);
    } catch (EngineException e) {
      throw GameExceptions.FailedToRetrieveLastLevel(Antonyms.NAME, difficulty, playerName, e);
    }
    int newLevel = lastCompletedLevel + 1;

    EntityRestriction maxCompleteTimeRes;
    try {
      maxCompleteTimeRes = rules.getEntityRestriction(fullName, "maxCompletionTime");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Antonyms.NAME, e);
    }

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

    Antonyms toCreate = new Antonyms();
    toCreate.setMaxCompletionTime(Long.parseLong("" + generator.generateIntDataValue(maxCompleteTimeRes.getDataRange())));
    toCreate.setPlayerName(playerName);
    toCreate.setLevel(newLevel);
    toCreate.setDifficulty(difficulty);
    try {
      generator.upsertGame(toCreate);
    } catch (EngineException e) {
      throw GameExceptions.GenerationError(Antonyms.NAME, e);
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

    words.add(antonym);
    Collections.shuffle(words, new Random(System.currentTimeMillis()));
    try {
      generator.createObjRelation(toCreate.getId(), hasMainWordRes.getOnProperty(), mainWord.getId());
      for (Word word : words) {
        generator.createObjRelation(toCreate.getId(), hasWordRes.getOnProperty(), word.getId());
      }
    } catch (EngineException e) {
      throw GameExceptions.GenerationError(Antonyms.NAME, e);
    }

    return toResponse(toCreate, mainWord, words);
  }

  @Override
  public AntonymResponse getGame(String id, String player) throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)) {
      throw GameExceptions.InvalidRequest(Antonyms.NAME);
    }

    Antonyms antonyms;
    try {
      antonyms = generator.getGameWithId(id, player, Antonyms.class);
    } catch (EngineException e) {
      throw GameExceptions.UnableToRetrieveGame(Antonyms.NAME, id, player, e);
    }

    List<Word> words;
    try{
      words = wordProvider.selectWordsByEntityId(antonyms.getId());
    } catch (ProviderException e) {
      throw GameExceptions.FailedToRetrieveWord(Antonyms.NAME, antonyms.getId(), e);
    }

    Word mainWord;
    try {
      String wordNode = dao.getMainWord(antonyms.getId());
      mainWord = wordProvider.selectWordByNode(wordNode);
    } catch (ProviderException e) {
      throw GameExceptions.FailedToRetrieveWord(Antonyms.NAME, antonyms.getId(), e);
    }
    removeWordFromList(mainWord, words);
    return toResponse(antonyms, mainWord, words);
  }

  @Override
  public AntonymResponse solveGame(String id, String player, Long completionTime, String solution) throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)
      || completionTime == null
      || solution.isEmpty()) {
      throw GameExceptions.InvalidRequest(Antonyms.NAME);
    }

    Antonyms antonyms;
    try {
      antonyms = generator.getGameWithId(id, player, Antonyms.class);
    } catch (EngineException e) {
      throw GameExceptions.UnableToRetrieveGame(Antonyms.NAME, id, player, e);
    }

    if (completionTime > antonyms.getMaxCompletionTime()) {
      throw GameExceptions.SurpassedMaxCompletionTime(Antonyms.NAME, id, antonyms.getMaxCompletionTime());
    }
    if (!StringUtils.isEmpty(antonyms.getCompletedDate())) {
      throw GameExceptions.GameIsAlreadySolvedAt(Antonyms.NAME, id, antonyms.getCompletedDate());
    }

    boolean areAntonyms;
    Word mainWord;
    List<Word> words;
    try {
      Word solutionWord = wordProvider.getWordWithValue(solution);
      words = wordProvider.selectWordsByEntityId(antonyms.getId());
      Word found = words.stream()
        .filter(x -> x.getId().equals(solutionWord.getId()))
        .findFirst()
        .orElse(null);
      if (found == null) {
        throw GameExceptions.UnableToSolve(Antonyms.NAME, "Provided word is not associated with this game");
      }

      String mainWordId = dao.getMainWord(antonyms.getId());
      mainWord = wordProvider.selectWordByNode(mainWordId);
      if (mainWord.getId() == null) {
        throw GameExceptions.FailedToRetrieveWord(Antonyms.NAME, mainWordId);
      }

      areAntonyms = wordProvider.areAntonyms(mainWord, solutionWord);

    } catch (ProviderException e) {
      throw GameExceptions.UnableToSolve(Antonyms.NAME, e);
    }

    if (areAntonyms) {
      antonyms.setCompletionTime(completionTime);
      antonyms.setCompletedDate(String.valueOf(System.currentTimeMillis()));
      try {
        generator.upsertGame(antonyms);
      } catch (EngineException e) {
        throw  GameExceptions.GenerationError(Antonyms.NAME, e);
      }
    }

    removeWordFromList(mainWord, words);
    return toResponse(antonyms, mainWord, words);
  }

  void removeWordFromList(Word toRemove, List<Word> words) {
    Word main = words.stream()
      .filter(x -> x.getId().equals(toRemove.getId()))
      .findFirst()
      .get();

    words.remove(main);
  }

  AntonymResponse toResponse(Antonyms antonyms, @Nullable Word mainWord, @Nullable List<Word> words) {
    AntonymResponse response = new AntonymResponse(antonyms);
    response.setSolved(antonyms.getCompletedDate() != null);
    if (mainWord != null) {
      response.setWord(mainWord.getValue());
    }
    if (words != null) {
      response.setChoices(words.stream().map(Word::getValue).collect(Collectors.toList()));
    }
    return response;
  }
}
