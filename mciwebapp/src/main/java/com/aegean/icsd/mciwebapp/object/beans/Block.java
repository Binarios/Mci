package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.core.annotations.DataProperty;
import com.aegean.icsd.engine.core.annotations.Entity;
import com.aegean.icsd.engine.core.annotations.Key;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;

@Entity(Block.NAME)
public class Block extends BaseGameObject {
  public static final String NAME = "Block";

  @Key
  @DataProperty("isMovingBlock")
  private Boolean moving;

  @Key
  @DataProperty("hasMovement")
  private String movement;

  @Key
  @DataProperty("hasDiagonalMovement")
  private String diagonalMovement;

  public boolean istMoving() {
    return moving;
  }

  public void setMoving(Boolean moving) {
    this.moving = moving;
  }

  public String getMovement() {
    return movement;
  }

  public void setMovement(String movement) {
    this.movement = movement;
  }

  public String getDiagonalMovement() {
    return diagonalMovement;
  }

  public void setDiagonalMovement(String diagonalMovement) {
    this.diagonalMovement = diagonalMovement;
  }
}
