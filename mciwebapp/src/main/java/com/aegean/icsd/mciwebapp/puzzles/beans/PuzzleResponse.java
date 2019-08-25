package com.aegean.icsd.mciwebapp.puzzles.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class PuzzleResponse extends ServiceResponse<Puzzle> {

  private List<PuzzlePiece> pieces;

  private String imageUrl;

  public PuzzleResponse(Puzzle game) {
    super(game);
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public List<PuzzlePiece> getPieces() {
    return pieces;
  }

  public void setPieces(List<PuzzlePiece> pieces) {
    this.pieces = pieces;
  }
}
