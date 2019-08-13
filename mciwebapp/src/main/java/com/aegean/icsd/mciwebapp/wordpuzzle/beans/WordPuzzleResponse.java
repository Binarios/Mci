package com.aegean.icsd.mciwebapp.wordpuzzle.beans;

import java.util.List;

public class WordPuzzleResponse {
  private WordPuzzle puzzle;
  private Boolean solved;
  private List<String> letters;

  public WordPuzzle getPuzzle() {
    return puzzle;
  }

  public void setPuzzle(WordPuzzle puzzle) {
    this.puzzle = puzzle;
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
