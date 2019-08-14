package com.aegean.icsd.mciwebapp.common.beans;

import com.aegean.icsd.engine.generator.beans.BaseGame;

public class ServiceResponse<T extends BaseGame> {
  private T game;


  protected ServiceResponse(T game) {
    this.game = game;
  }

  public T getGame() {
    return game;
  }
}
