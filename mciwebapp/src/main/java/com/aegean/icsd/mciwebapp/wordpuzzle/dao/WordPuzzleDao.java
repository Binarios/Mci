package com.aegean.icsd.mciwebapp.wordpuzzle.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WordPuzzle;

@Repository
public class WordPuzzleDao implements IWordPuzzleDao {
  @Override public int getLastCompletedLevel(Difficulty difficulty, String playerName) throws MciException {
    return 0;
  }

  @Override public List<WordPuzzle> getGamesForPlayer(String playerName) throws MciException {
    return null;
  }

  @Override public WordPuzzle getById(String id, String player) throws MciException {
    return null;
  }

  @Override public String getWordById(String id) throws MciException {
    return null;
  }

  @Override public boolean solveGame(String id, String player, String key, Integer value) throws MciException {
    return false;
  }
}
