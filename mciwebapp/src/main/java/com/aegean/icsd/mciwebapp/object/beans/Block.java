package com.aegean.icsd.mciwebapp.object.beans;

import com.aegean.icsd.engine.annotations.DataProperty;
import com.aegean.icsd.engine.annotations.Entity;
import com.aegean.icsd.engine.annotations.Id;
import com.aegean.icsd.engine.annotations.Key;

@Entity(Block.NAME)
public class Block {
  public static final String NAME = "Block";

  @Id
  @DataProperty("hasId")
  private String id;

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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
