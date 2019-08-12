package com.aegean.icsd.mciwebapp.wordpuzzle.implementations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WordPuzzle;
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WordPuzzleResponse;
import com.aegean.icsd.mciwebapp.wordpuzzle.dao.IWordPuzzleDao;
import com.aegean.icsd.mciwebapp.wordpuzzle.interfaces.IWordPuzzleSvc;

@Service
public class WordPuzzleSvc implements IWordPuzzleSvc {

  @Autowired
  private IWordPuzzleDao dao;

  @Override
  public List<WordPuzzleResponse> getWordPuzzles(String playerName) throws MciException {
    if (StringUtils.isEmpty(playerName)) {
      throw Exceptions.InvalidRequest();
    }
    List<WordPuzzle> puzzles = dao.getGamesForPlayer(playerName);
    List<WordPuzzleResponse> res = new ArrayList<>();
    for (WordPuzzle puzzle : puzzles) {
      String word = dao.getWordById(puzzle.getId());
      res.add(toResponse(puzzle, word));
    }
    return res;
  }

  @Override
  public WordPuzzleResponse createWordPuzzle(String playerName, Difficulty difficulty) throws MciException {
    return null;
  }

  @Override
  public WordPuzzleResponse getWordPuzzle(String id, String player) throws MciException {
    return null;
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
