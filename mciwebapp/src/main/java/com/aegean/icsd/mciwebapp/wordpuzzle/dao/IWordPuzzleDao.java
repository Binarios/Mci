package com.aegean.icsd.mciwebapp.wordpuzzle.dao;

import com.aegean.icsd.mciwebapp.common.beans.MciException;

public interface IWordPuzzleDao {

  boolean solveGame(String id, String player, String word) throws MciException;

  String getAssociatedWordNodeById(String id) throws MciException;
}
