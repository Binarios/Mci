package com.aegean.icsd.mciobjects.pieces.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.common.daos.IObjectsDao;
import com.aegean.icsd.mciobjects.common.implementations.ProviderExceptions;
import com.aegean.icsd.mciobjects.images.beans.Image;
import com.aegean.icsd.mciobjects.images.interfaces.IImageProvider;
import com.aegean.icsd.mciobjects.pieces.beans.Piece;
import com.aegean.icsd.mciobjects.pieces.interfaces.IPieceProvider;

@Service
public class PieceProvider implements IPieceProvider {

  @Autowired
  private Map<String, EntityRestriction> pieceRules;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IObjectsDao dao;

  @Override
  public List<Piece> getPieces(int count) throws ProviderException {
    List<Piece> pieces = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Piece toCreate = new Piece();

      try {
        generator.upsertGameObject(toCreate);
      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(Piece.NAME, e);
      }

      pieces.add(toCreate);
    }
    return pieces;
  }

  @Override
  public void connectPieces(Piece thisPiece, Piece otherPiece) throws ProviderException {
    EntityRestriction hasConnectingPiece = pieceRules.get("hasConnectingPiece");
    try {
      generator.createObjRelation(thisPiece, otherPiece, hasConnectingPiece.getOnProperty());
    } catch (EngineException e) {
      throw ProviderExceptions.GenerationError(Piece.NAME, e);
    }
  }

  @Override
  public void setPieceImage(Piece piece, Image image) throws ProviderException {
    EntityRestriction hasImage = pieceRules.get("hasImage");
    try {
      generator.createObjRelation(piece, image, hasImage.getOnProperty());
    } catch (EngineException e) {
      throw ProviderExceptions.GenerationError(Piece.NAME, e);
    }
  }

  @Override
  public List<Piece> selectPiecesForEntityOnProperty(String entityId, EntityProperty oProperty)
    throws ProviderException {
    List<String> ids = dao.getAssociatedIdsOnPropertyForEntityId(entityId, oProperty, Piece.class);
    List<Piece> pieces = new ArrayList<>();
    for (String id : ids) {
      Piece piece = new Piece();
      piece.setId(id);
      try {
        piece = generator.selectGameObject(piece).get(0);
      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetObject(Piece.NAME, e);
      }
      pieces.add(piece);
    }
    return pieces;
  }

  @Override
  public List<Piece> selectConnectingPieces(String pieceId) throws ProviderException {
    return selectPiecesForEntityOnProperty(pieceId, pieceRules.get("hasConnectingPiece").getOnProperty());
  }
}
