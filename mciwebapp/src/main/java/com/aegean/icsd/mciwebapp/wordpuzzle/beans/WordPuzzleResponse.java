package com.aegean.icsd.mciwebapp.wordpuzzle.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class WordPuzzleResponse extends ServiceResponse<WordPuzzle> {
  private Boolean solved;
  private List<String> letters;

  public WordPuzzleResponse(WordPuzzle game) {
    super(game);
  }


  public Boolean getSolved() {
    return solved;
  }

  public void setSolved(Boolean solved) {
    this.solved = solved;
  }

  public List<String> getLetters() {
    return letters;
  }

  public void setLetters(List<String> letters) {
    this.letters = letters;
  }
}
