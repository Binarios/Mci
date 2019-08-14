package com.aegean.icsd.mciwebapp.wordpuzzle.implementations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WordPuzzle;
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WordPuzzleResponse;
import com.aegean.icsd.mciwebapp.wordpuzzle.dao.IWordPuzzleDao;
import com.aegean.icsd.mciwebapp.wordpuzzle.interfaces.IWordPuzzleSvc;

@Service
public class WordPuzzleSvc implements IWordPuzzleSvc {

  private static Logger LOGGER = Logger.getLogger(WordPuzzleSvc.class);
  private static final String gameName = "WordPuzzle";

  @Autowired
  private IRules rules;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private IWordPuzzleDao dao;

  @Override
  public List<WordPuzzleResponse> getWordPuzzles(String playerName) throws MciException {
    if (StringUtils.isEmpty(playerName)) {
      throw GameExceptions.InvalidRequest(gameName);
    }
    List<WordPuzzle> puzzles;
    try {
      puzzles = generator.getGamesForPlayer(gameName, playerName, WordPuzzle.class);
    } catch (EngineException e) {
      throw GameExceptions.FailedToRetrieveGames(gameName, playerName, e);
    }
    List<WordPuzzleResponse> res = new ArrayList<>();
    for (WordPuzzle puzzle : puzzles) {
      res.add(toResponse(puzzle, ""));
    }
    return res;
  }

  @Override
  public WordPuzzleResponse createWordPuzzle(String playerName, Difficulty difficulty) throws MciException {
    LOGGER.info(String.format("Creating Word Puzzle game for player %s with difficulty %s",
      playerName, difficulty.name()));

    if (StringUtils.isEmpty(playerName)) {
      throw GameExceptions.InvalidRequest(gameName);
    }

    String fullName = Utils.getFullGameName(gameName, difficulty);

    int lastCompletedLevel;
    try {
      lastCompletedLevel = generator.getLastCompletedLevel(gameName, difficulty, playerName);
    } catch (EngineException e) {
      throw GameExceptions.FailedToRetrieveLastLevel(gameName, difficulty, playerName, e);
    }
    int newLevel = lastCompletedLevel + 1;

    EntityRestriction maxCompleteTimeRes;
    try {
      maxCompleteTimeRes = rules.getEntityRestriction(fullName, "maxCompletionTime");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(gameName, e);
    }

    EntityRestriction wordLengthRes;
    try {
      wordLengthRes = rules.getEntityRestriction(fullName, "hasWordLength");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(gameName, e);
    }

    EntityRestriction hasWordRes;
    try {
      hasWordRes = rules.getEntityRestriction(fullName, "hasWord");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(gameName, e);
    }

    WordPuzzle puzzle = new WordPuzzle();
    puzzle.setDifficulty(difficulty);
    puzzle.setPlayerName(playerName);
    puzzle.setLevel(newLevel);
    puzzle.setMaxCompletionTime(Long.parseLong("" + generator.generateIntDataValue(maxCompleteTimeRes.getDataRange())));
    puzzle.setWordLength(generator.generateIntDataValue(wordLengthRes.getDataRange()));

    Word word;
    try {
      word = wordProvider.getNewWordFor(fullName, puzzle.getWordLength());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(gameName, e);
    }

    try {
      generator.upsertObj(puzzle);
      generator.createObjRelation(puzzle.getId(), hasWordRes.getOnProperty(), word.getId());
    } catch (EngineException e) {
      throw GameExceptions.GenerationError(gameName, e);
    }

    return toResponse(puzzle, word.getValue());
  }

  @Override
  public WordPuzzleResponse getWordPuzzle(String id, String player) throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)) {
      throw GameExceptions.InvalidRequest(gameName);
    }

    WordPuzzle puzzle;
    try {
      puzzle = generator.getGameWithId(id, player, WordPuzzle.class);
    } catch (EngineException e) {
      throw GameExceptions.UnableToRetrieveGame(gameName, id, player);
    }

    String wordNode = dao.getAssociatedWordNodeById(puzzle.getId());
    Word word;
    try{
      word = wordProvider.getWordFromNode(wordNode);
    } catch (ProviderException e) {
      throw GameExceptions.FailedToRetrieveWord(gameName, puzzle.getId());
    }

    return toResponse(puzzle, word.getValue());
  }

  @Override
  public WordPuzzleResponse solveGame(String id, String player, Long completionTime, String solution)
      throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)
      || completionTime == null
      || solution.isEmpty()) {
      throw GameExceptions.InvalidRequest(gameName);
    }

    WordPuzzle puzzle;
    try {
      puzzle = generator.getGameWithId(id, player, WordPuzzle.class);
    } catch (EngineException e) {
      throw GameExceptions.UnableToRetrieveGame(gameName, id, player);
    }

    if (completionTime > puzzle.getMaxCompletionTime()) {
      throw GameExceptions.SurpassedMaxCompletionTime(gameName, id, puzzle.getMaxCompletionTime());
    }
    if (!StringUtils.isEmpty(puzzle.getCompletedDate())) {
      throw GameExceptions.GameIsAlreadySolvedAt(gameName, id, puzzle.getCompletedDate());
    }

    boolean solved = dao.solveGame(puzzle.getId(), player, solution);
    String wordNode = dao.getAssociatedWordNodeById(puzzle.getId());
    Word word;
    try{
      word = wordProvider.getWordFromNode(wordNode);
    } catch (ProviderException e) {
      throw GameExceptions.FailedToRetrieveWord(gameName, puzzle.getId());
    }
    if (solved) {
      puzzle.setCompletionTime(completionTime);
      puzzle.setCompletedDate(String.valueOf(System.currentTimeMillis()));
      try {
        generator.upsertObj(puzzle);
      } catch (EngineException e) {
        throw  GameExceptions.GenerationError(gameName, e);
      }
    }

    WordPuzzleResponse resp = toResponse(puzzle, word.getValue());
    resp.setSolved(solved);
    return resp;
  }

  private WordPuzzleResponse toResponse(WordPuzzle puzzle, String word) {
    String[] letters = word.split("");
    List<String> shuffled = Arrays.asList(letters);
    Collections.shuffle(shuffled, new Random(System.currentTimeMillis()));
    WordPuzzleResponse res = new WordPuzzleResponse(puzzle);
    res.setLetters(shuffled);
    return res;
  }
}
