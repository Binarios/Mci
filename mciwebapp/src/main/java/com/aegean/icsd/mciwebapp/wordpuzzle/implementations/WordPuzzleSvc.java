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
import com.aegean.icsd.mciwebapp.common.beans.MciException;
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
      throw Exceptions.InvalidRequest();
    }
    List<WordPuzzle> puzzles = null;
    try {
      puzzles = generator.getGamesForPlayer(playerName, playerName, WordPuzzle.class);
    } catch (EngineException e) {
      throw Exceptions.FailedToRetrieveGames(playerName, e);
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
      throw Exceptions.InvalidRequest();
    }

    String fullName = Utils.getFullGameName(gameName, difficulty);

    int lastCompletedLevel;
    try {
      lastCompletedLevel = generator.getLastCompletedLevel(gameName, difficulty, playerName);
    } catch (EngineException e) {
      throw Exceptions.FailedToRetrieveLastLevel(gameName, difficulty, playerName, e);
    }
    int newLevel = lastCompletedLevel + 1;

    EntityRestriction maxCompleteTimeRes;
    try {
      maxCompleteTimeRes = rules.getEntityRestriction(fullName, "maxCompletionTime");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveGameRules(e);
    }

    EntityRestriction wordLengthRes;
    try {
      wordLengthRes = rules.getEntityRestriction(fullName, "hasWordLength");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveGameRules(e);
    }

    WordPuzzle puzzle = new WordPuzzle();
    puzzle.setDifficulty(difficulty);
    puzzle.setPlayerName(playerName);
    puzzle.setLevel(newLevel);
    puzzle.setMaxCompletionTime(Long.parseLong("" + generator.generateIntDataValue(maxCompleteTimeRes.getDataRange())));
    puzzle.setWordLength(generator.generateIntDataValue(wordLengthRes.getDataRange()));

    String word = wordProvider.getWord

    return null;
  }

  @Override
  public WordPuzzleResponse getWordPuzzle(String id, String player) throws MciException {
    if (StringUtils.isEmpty(id)
      || StringUtils.isEmpty(player)) {
      throw Exceptions.InvalidRequest();
    }

    WordPuzzle puzzle;
    try {
      puzzle = generator.getGameWithId(id, player, WordPuzzle.class);
    } catch (EngineException e) {
      throw Exceptions.UnableToRetrieveGame(id, player);
    }

    if (puzzle == null) {
      throw Exceptions.UnableToRetrieveGame(id, player);
    }

    String word = dao.getWordById(puzzle.getId());
    if(StringUtils.isEmpty(word)) {
      throw Exceptions.FailedToRetrieveWord(puzzle.getId());
    }
    return toResponse(puzzle, word);
  }

  @Override
  public WordPuzzleResponse solveGame(String id, String player, Long completionTime, String solution)
      throws MciException {
    return null;
  }

  private WordPuzzleResponse toResponse(WordPuzzle puzzle, String word) {
    String[] letters = word.split("");
    List<String> shuffled = Arrays.asList(letters);
    Collections.shuffle(shuffled, new Random(System.currentTimeMillis()));
    WordPuzzleResponse res = new WordPuzzleResponse();
    res.setPuzzle(puzzle);
    res.setLetters(shuffled);
    return res;
  }
}
