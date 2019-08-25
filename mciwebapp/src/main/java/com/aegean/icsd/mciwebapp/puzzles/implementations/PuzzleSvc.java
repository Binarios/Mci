package com.aegean.icsd.mciwebapp.puzzles.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.images.beans.Image;
import com.aegean.icsd.mciobjects.images.interfaces.IImageProvider;
import com.aegean.icsd.mciobjects.pieces.beans.Piece;
import com.aegean.icsd.mciobjects.pieces.interfaces.IPieceProvider;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.puzzles.beans.Puzzle;
import com.aegean.icsd.mciwebapp.puzzles.beans.PuzzleResponse;
import com.aegean.icsd.mciwebapp.puzzles.interfaces.IPuzzleSvc;

@Service
public class PuzzleSvc extends AbstractGameSvc<Puzzle, PuzzleResponse> implements IPuzzleSvc {

  @Autowired
  private IRules rules;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IPieceProvider pieceProvider;

  @Autowired
  private IImageProvider imageProvider;

  @Override
  protected void handleDataTypeRestrictions(String fullName, Puzzle toCreate) throws MciException {
    EntityRestriction hasRows;
    EntityRestriction hasColumns;

    try {
      hasRows = rules.getEntityRestriction(fullName, "hasRows");
      hasColumns = rules.getEntityRestriction(fullName, "hasColumns");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(Puzzle.NAME, e);
    }

    Long rows = generator.generateLongDataValue(hasRows.getDataRange());
    Long columns = generator.generateLongDataValue(hasColumns.getDataRange());

    toCreate.setColumns(Integer.parseInt(columns.toString()));
    toCreate.setRows(Integer.parseInt(rows.toString()));
  }

  @Override
  protected void handleObjectRestrictions(String fullName, Puzzle toCreate) throws MciException {
    EntityRestriction hasCornerPiece;
    EntityRestriction hasBorderLinePiece;
    EntityRestriction hasInteriorPiece;
    EntityRestriction hasImage;

    try {
      hasCornerPiece = rules.getEntityRestriction(fullName, "hasCornerPiece");
      hasBorderLinePiece = rules.getEntityRestriction(fullName, "hasBorderLinePiece");
      hasInteriorPiece = rules.getEntityRestriction(fullName, "hasInteriorPiece");
      hasImage = rules.getEntityRestriction(fullName, "hasImage");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(Puzzle.NAME, e);
    }

    Image image;
    List<Piece> pieces;
    int totalPieces = toCreate.getColumns() * toCreate.getRows();
    try {
      image = imageProvider.getNewImagesFor(fullName, hasImage.getCardinality(), new Image()).get(0);
      pieces = pieceProvider.getPieces(totalPieces);
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(Puzzle.NAME, e);
    }

    List<Piece> cornerPieces = new ArrayList<>();
    List<Piece> interiorPieces = new ArrayList<>();
    List<Piece> borderLinePieces = new ArrayList<>();
    Piece[][] pieceMap = new Piece[toCreate.getRows()][toCreate.getColumns()];
    for (int row = 0; row < toCreate.getRows(); row++) {
      for (int col = 0; col < toCreate.getColumns(); col++) {
        Piece piece = pieces.remove(0);
        pieceMap[row][col] = piece;
        if (isCorner(row, col, toCreate.getRows(), toCreate.getColumns())) {
          cornerPieces.add(piece);
        } else if (isBorderPiece(row, col, toCreate.getRows(), toCreate.getColumns())) {
          borderLinePieces.add(piece);
        } else {
          interiorPieces.add(piece);
        }
        try {
          if (col > 0) {
            pieceProvider.connectPieces(piece, pieceMap[row][col -1]);
          }
          if (row > 0) {
            pieceProvider.connectPieces(piece, pieceMap[row - 1][col]);
          }
        } catch (ProviderException e) {
          throw GameExceptions.GenerationError(Puzzle.NAME, e);
        }

      }
    }
    createObjRelation(toCreate, image, hasImage.getOnProperty());
    createObjRelation(toCreate, cornerPieces, hasCornerPiece.getOnProperty());
    createObjRelation(toCreate, interiorPieces, hasInteriorPiece.getOnProperty());
    createObjRelation(toCreate, borderLinePieces, hasBorderLinePiece.getOnProperty());
  }


  @Override
  protected boolean isValid(Object solution) {
    return solution != null && !((Map)solution).isEmpty();
  }

  @Override
  protected boolean checkSolution(Puzzle game, Object solution) throws MciException {
    Map<String, List<String>> casted = (Map) solution;
    EntityRestriction hasCornerPiece;
    EntityRestriction hasBorderLinePiece;
    EntityRestriction hasInteriorPiece;

    try {
      hasCornerPiece = rules.getEntityRestriction(getFullGameName(game), "hasCornerPiece");
      hasBorderLinePiece = rules.getEntityRestriction(getFullGameName(game), "hasBorderLinePiece");
      hasInteriorPiece = rules.getEntityRestriction(getFullGameName(game), "hasInteriorPiece");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(Puzzle.NAME, e);
    }

    List<Piece> cornerBlocks;
    List<Piece> borderPieces;
    List<Piece> interiorBlocks;
    try {
      cornerBlocks = pieceProvider.selectPiecesForEntityOnProperty(game.getId(), hasCornerPiece.getOnProperty());
      borderPieces = pieceProvider.selectPiecesForEntityOnProperty(game.getId(), hasBorderLinePiece.getOnProperty());
      interiorBlocks = pieceProvider.selectPiecesForEntityOnProperty(game.getId(), hasInteriorPiece.getOnProperty());
    } catch (ProviderException e) {
      throw GameExceptions.UnableToResponse(Puzzle.NAME, e);
    }

    List<Piece> totalPieces = new ArrayList<>();
    totalPieces.addAll(cornerBlocks);
    totalPieces.addAll(borderPieces);
    totalPieces.addAll(interiorBlocks);

    boolean solved = true;

    for (Map.Entry<String, List<String>> entry : casted.entrySet()) {
      String pieceId = entry.getKey();
      Piece found = totalPieces.stream()
        .filter(x -> x.getId().equals(pieceId))
        .findFirst()
        .orElse(null);

      if (found == null) {
        solved = false;
        break;
      }

      List<Piece> connectingPieces;
      try {
        connectingPieces = pieceProvider.selectConnectingPieces(pieceId);
      } catch (ProviderException e) {
        throw GameExceptions.UnableToSolve(Puzzle.NAME, e);
      }

      if (connectingPieces.size() != entry.getValue().size()) {
        solved = false;
      } else {
        for (Piece connectedPiece : connectingPieces) {
          solved &= entry.getValue().contains(connectedPiece.getId());
        }
      }
    }
    return solved;
  }

  @Override
  protected PuzzleResponse toResponse(Puzzle game) throws MciException {
    EntityRestriction hasCornerPiece;
    EntityRestriction hasBorderLinePiece;
    EntityRestriction hasInteriorPiece;

    try {
      hasCornerPiece = rules.getEntityRestriction(getFullGameName(game), "hasCornerPiece");
      hasBorderLinePiece = rules.getEntityRestriction(getFullGameName(game), "hasBorderLinePiece");
      hasInteriorPiece = rules.getEntityRestriction(getFullGameName(game), "hasInteriorPiece");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(Puzzle.NAME, e);
    }

    Image image;
    List<Piece> cornerBlocks;
    List<Piece> borderPieces;
    List<Piece> interiorBlocks;
    try {
      image = imageProvider.selectImagesByEntityId(game.getId()).get(0);
      cornerBlocks = pieceProvider.selectPiecesForEntityOnProperty(game.getId(), hasCornerPiece.getOnProperty());
      borderPieces = pieceProvider.selectPiecesForEntityOnProperty(game.getId(), hasBorderLinePiece.getOnProperty());
      interiorBlocks = pieceProvider.selectPiecesForEntityOnProperty(game.getId(), hasInteriorPiece.getOnProperty());
    } catch (ProviderException e) {
      throw GameExceptions.UnableToResponse(Puzzle.NAME, e);
    }

    List<Piece> pieces = new ArrayList<>();
    pieces.addAll(cornerBlocks);
    pieces.addAll(borderPieces);
    pieces.addAll(interiorBlocks);
    Collections.shuffle(pieces, new Random(System.currentTimeMillis()));

    List<String> puzzlePieces = pieces.stream().map(Piece::getId).collect(Collectors.toList());

    Collections.shuffle(puzzlePieces, new Random(System.currentTimeMillis()));
    PuzzleResponse response = new PuzzleResponse(game);
    response.setPieces(puzzlePieces);
    response.setImageUrl(image.getPath());
    return response;
  }


  boolean isCorner(int row, int col, int maxRow, int maxCol) {
    return (row == 0 && col ==0)
      || (row == 0 && col == maxCol -1)
      || (row == maxRow - 1 && col == 0)
      || (row == maxRow - 1 && col == maxCol -1);
  }

  boolean isBorderPiece(int row, int col, Integer rows, Integer columns) {
    return row == 0 || row == rows - 1 || col == 0 || col == columns - 1;
  }
}
