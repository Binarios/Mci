package com.aegean.icsd.mciwebapp.common.beans;

import com.aegean.icsd.engine.generator.beans.BaseGame;

public class ServiceResponse<T extends BaseGame> {
  private T game;
  private Boolean solved;

  public ServiceResponse(T game) {
    this.game = game;
  }

  public T getGame() {
    return game;
  }

  public boolean isSolved() {
    return solved;
  }

  public void setSolved(boolean solved) {
    this.solved = solved;
  }
}
