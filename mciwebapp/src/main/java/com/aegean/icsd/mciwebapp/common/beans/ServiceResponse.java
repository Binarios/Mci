package com.aegean.icsd.mciwebapp.common.beans;

import org.apache.commons.lang3.StringUtils;

import com.aegean.icsd.engine.common.beans.BaseGame;

public class ServiceResponse<T extends BaseGame> {
  private T game;
  private Boolean solved;

  public ServiceResponse(T game) {
    this.game = game;
    this.solved = !StringUtils.isEmpty(game.getCompletedDate());
  }

  public T getGame() {
    return game;
  }

  public Boolean isSolved() {
    return solved;
  }

}
