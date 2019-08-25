package com.aegean.icsd.mciwebapp.puzzles.beans;

import java.util.List;

public class PuzzlePiece {
  private String id;
  private String imagePath;
  private List<String> connectedIds;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public List<String> getConnectedIds() {
    return connectedIds;
  }

  public void setConnectedIds(List<String> connectedIds) {
    this.connectedIds = connectedIds;
  }
}
