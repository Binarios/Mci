package com.aegean.icsd.mciwebapp.wordpuzzle.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class WordPuzzleResponse extends ServiceResponse<WordPuzzle> {

  private List<String> letters;

  public WordPuzzleResponse(WordPuzzle game) {
    super(game);
  }

  public List<String> getLetters() {
    return letters;
  }

  public void setLetters(List<String> letters) {
    this.letters = letters;
  }
}
