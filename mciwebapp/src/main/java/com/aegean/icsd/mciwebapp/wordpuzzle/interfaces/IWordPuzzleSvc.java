package com.aegean.icsd.mciwebapp.wordpuzzle.interfaces;

import java.util.List;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WordPuzzleResponse;

public interface IWordPuzzleSvc {

  List<WordPuzzleResponse> getWordPuzzles(String playerName) throws MciException;

  WordPuzzleResponse createWordPuzzle(String playerName, Difficulty difficulty) throws MciException;

  WordPuzzleResponse getWordPuzzle(String id, String player) throws MciException;

  WordPuzzleResponse solveGame(String id, String player, Long completionTime, String solution) throws MciException;

}
