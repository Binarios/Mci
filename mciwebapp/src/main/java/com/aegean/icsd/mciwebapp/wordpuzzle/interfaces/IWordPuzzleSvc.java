package com.aegean.icsd.mciwebapp.wordpuzzle.interfaces;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.interfaces.IGameService;
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WordPuzzleResponse;

public interface IWordPuzzleSvc extends IGameService<WordPuzzleResponse> {

  WordPuzzleResponse solveGame(String id, String player, Long completionTime, String solution) throws MciException;

}
