package com.aegean.icsd.mciwebapp.wordpuzzle.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.common.beans.BaseGame;

@Entity(WordPuzzle.NAME)
public class WordPuzzle extends BaseGame {
  public static final String NAME = "WordPuzzle";

  @DataProperty("hasWordLength")
  private Integer wordLength;

  public Integer getWordLength() {
    return wordLength;
  }

  public void setWordLength(Integer wordLength) {
    this.wordLength = wordLength;
  }
}
