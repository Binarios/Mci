package com.aegean.icsd.mciwebapp.memorycards.implementations;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.ImageData;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCards;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCardsResponse;
import com.aegean.icsd.mciwebapp.memorycards.interfaces.IMemoryCardsSvc;
import com.aegean.icsd.mciwebapp.object.beans.Image;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.interfaces.IImageProvider;

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

    if (Image.NAME.equals(hasObjectRes.getDataRange().getDataType())) {

    }

  }

  @Override
  protected boolean isValid(Object solution) {
    return !((List<String>)solution).isEmpty();
  }

  @Override
  protected boolean checkSolution(MemoryCards game, Object solution) throws MciException {
    return false;
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
