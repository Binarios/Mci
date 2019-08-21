package com.aegean.icsd.mciwebapp.memorycards.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.images.beans.Image;
import com.aegean.icsd.mciobjects.images.beans.ImageData;
import com.aegean.icsd.mciobjects.images.interfaces.IImageProvider;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCards;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCardsResponse;
import com.aegean.icsd.mciwebapp.memorycards.interfaces.IMemoryCardsSvc;

@Service
public class MemoryCardsSvc extends AbstractGameSvc<MemoryCards, MemoryCardsResponse> implements IMemoryCardsSvc {

  @Autowired
  private IRules rules;

  @Autowired
  private IImageProvider imageProvider;

  @Override
  protected void handleDataTypeRestrictions(String fullName, MemoryCards toCreate) throws MciException {
    EntityRestriction displayTimeRes;
    EntityRestriction objectsPerCardRes;
    try {
      displayTimeRes = rules.getEntityRestriction(fullName, "displayTime");
      objectsPerCardRes = rules.getEntityRestriction(fullName, "objectsPerCard");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(MemoryCards.NAME,e);
    }

    Long displayTime = Long.parseLong(displayTimeRes.getDataRange().getRanges().get(0).getValue());
    Integer objectsPerCard = Integer.parseInt(objectsPerCardRes.getDataRange().getRanges().get(0).getValue());
    toCreate.setDisplayTime(displayTime);
    toCreate.setObjectsPerCards(objectsPerCard);
  }

  @Override
  protected void handleObjectRestrictions(String fullName, MemoryCards toCreate) throws MciException {
    EntityRestriction hasObjectRes;
    try {
      hasObjectRes = rules.getEntityRestriction(fullName, "hasObject");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(MemoryCards.NAME,e);
    }

    List<BaseGameObject> objects = new ArrayList<>();
    if (Image.NAME.equals(hasObjectRes.getDataRange().getDataType())) {
      try {
        List<Image> images = imageProvider.selectNewImagesForEntity(fullName, hasObjectRes.getCardinality());
        objects.addAll(images);
      } catch (ProviderException e) {
        throw GameExceptions.GenerationError(MemoryCards.NAME, e);
      }
    }

    createObjRelation(toCreate, objects, hasObjectRes.getOnProperty());
  }

  @Override
  protected boolean isValid(Object solution) {
    return !((List<String>)solution).isEmpty();
  }

  @Override
  protected boolean checkSolution(MemoryCards game, Object solution) throws MciException {
    List<ImageData> castedSolution = (List) solution;
    List<Image> associatedImages;
    try {
      associatedImages = imageProvider.selectImagesByEntityId(game.getId());
    } catch (ProviderException e) {
      throw GameExceptions.UnableToResponse(MemoryCards.NAME, e);
    }

    List<ImageData> existingImageData = associatedImages.stream()
      .map(x -> {
        ImageData imageData = new ImageData();
        imageData.setPath(x.getPath());
        imageData.setId(x.getId());
        return imageData;
      }).collect(Collectors.toList());

    List<ImageData> notFound = castedSolution.stream()
      .filter(x -> {
        ImageData foundItem = existingImageData.stream()
          .filter(y -> y.getId().equals(x.getId()) && y.getPath().equals(x.getPath()))
          .findFirst()
          .orElse(null);
        return foundItem == null;
      }).collect(Collectors.toList());


    return notFound.isEmpty();
  }

  @Override
  protected MemoryCardsResponse toResponse(MemoryCards toCreate) throws MciException {
    List<Image> associatedImages;
    try {
      associatedImages = imageProvider.selectImagesByEntityId(toCreate.getId());
    } catch (ProviderException e) {
      throw GameExceptions.UnableToResponse(MemoryCards.NAME, e);
    }

    List<ImageData> imageData = associatedImages.stream().map(x -> {
      ImageData data = new ImageData();
      data.setId(x.getId());
      data.setPath(x.getPath());
      return data;
    }).collect(Collectors.toList());

    MemoryCardsResponse response = new MemoryCardsResponse(toCreate);
    response.setImages(imageData);

    return response;
  }
}
