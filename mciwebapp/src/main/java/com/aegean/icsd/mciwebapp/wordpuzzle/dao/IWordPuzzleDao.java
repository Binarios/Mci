package com.aegean.icsd.mciwebapp.wordpuzzle.dao;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WordPuzzle;

public interface IWordPuzzleDao {

  int getLastCompletedLevel(Difficulty difficulty, String playerName) throws MciException;

  List<WordPuzzle> getGamesForPlayer(String playerName) throws MciException;

  WordPuzzle getById(String id, String player) throws MciException;

  String getWordById(String id) throws MciException;

  boolean solveGame(String id, String player, String key, Integer value) throws MciException;
}
