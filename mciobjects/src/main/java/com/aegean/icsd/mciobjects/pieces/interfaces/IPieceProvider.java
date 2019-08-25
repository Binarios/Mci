package com.aegean.icsd.mciobjects.pieces.interfaces;

import java.util.List;

import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.pieces.beans.Piece;

public interface IPieceProvider {
  List<Piece> getPieces(int count) throws ProviderException;
  void connectPieces(Piece thisPiece, Piece otherPiece) throws ProviderException;

  List<Piece> selectPiecesForEntityOnProperty(String entityId, EntityProperty oProperty) throws ProviderException;

  List<Piece> selectConnectingPieces(String pieceId) throws ProviderException;
}
