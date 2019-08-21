package com.aegean.icsd.mciwebapp.findthesounds.implementations;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.images.beans.Image;
import com.aegean.icsd.mciobjects.images.beans.ImageData;
import com.aegean.icsd.mciobjects.images.interfaces.IImageProvider;
import com.aegean.icsd.mciobjects.sounds.beans.Sound;
import com.aegean.icsd.mciobjects.sounds.interfaces.ISoundProvider;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.findthesounds.beans.FindTheSound;
import com.aegean.icsd.mciwebapp.findthesounds.beans.FindTheSoundResponse;
import com.aegean.icsd.mciwebapp.findthesounds.beans.Solution;
import com.aegean.icsd.mciwebapp.findthesounds.interfaces.IFindTheSoundSvc;

@Service
public class FindTheSoundSvc extends AbstractGameSvc<FindTheSound, FindTheSoundResponse> implements IFindTheSoundSvc {

  @Autowired
  private IRules rules;

  @Autowired
  private ISoundProvider soundProvider;

  @Autowired
  private IImageProvider imageProvider;


  @Override
  protected void handleDataTypeRestrictions(String fullName, FindTheSound toCreate) throws MciException {
    // nothing to implement
  }

  @Override
  protected void handleObjectRestrictions(String fullName, FindTheSound toCreate) throws MciException {
    EntityRestriction hasSoundRes;
    EntityRestriction hasImageRes;

    try {
      hasSoundRes = rules.getEntityRestriction(fullName, "hasSound");
      hasImageRes = rules.getEntityRestriction(fullName, "hasImage");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(FindTheSound.NAME, e);
    }

    Sound soundToUse;
    List<Image> images;
    Sound soundCriteria = new Sound();
    soundCriteria.setImageAssociated(true);
    Image imageCriteria = new Image();
    imageCriteria.setSoundAssociated(true);
    try {
      List<Sound> sounds = soundProvider.getNewSoundsFor(fullName, hasSoundRes.getCardinality(), soundCriteria);
      images = imageProvider.getNewImagesFor(fullName, hasImageRes.getCardinality(), imageCriteria);
      soundToUse = sounds.get(0);
      List<Image> relatedImages = imageProvider.selectImagesByEntityId(soundToUse.getId());

      Collections.shuffle(images, new Random(System.currentTimeMillis()));
      if (!relatedImages.isEmpty()) {
        images.subList(0, relatedImages.size()).clear();
      }

      images.addAll(relatedImages);

    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(FindTheSound.NAME, e);
    }

    createObjRelation(toCreate, soundToUse, hasSoundRes.getOnProperty());
    createObjRelation(toCreate, images, hasImageRes.getOnProperty());

  }

  @Override
  protected boolean isValid(Object solution) {
    Solution castedSolution = (Solution) solution;

    return castedSolution != null
      && !StringUtils.isEmpty(castedSolution.getImageId())
      && !StringUtils.isEmpty(castedSolution.getSoundId()) ;
  }

  @Override
  protected boolean checkSolution(FindTheSound game, Object solution) throws MciException {
    Solution castedSolution = (Solution) solution;

    List<Image> existingImages;
    List<Sound> existingSounds;
    try {
      existingImages = imageProvider.selectImagesByEntityId(game.getId());
      existingSounds = soundProvider.selectSoundsByEntityId(game.getId());
    } catch (ProviderException e) {
      throw GameExceptions.UnableToSolve(FindTheSound.NAME, e);
    }

    Sound sound = existingSounds.stream()
      .filter(x -> x.getId().equals(castedSolution.getSoundId()))
      .findFirst()
      .orElse(null);

    Image image = existingImages.stream()
      .filter(x -> x.getId().equals(castedSolution.getImageId()))
      .findFirst()
      .orElse(null);

    if (sound == null || image == null) {
      return false;
    }

    boolean isAssociatedWithImage;
    boolean isAssociatedWithSound;
    try {
      isAssociatedWithImage = soundProvider.isAssociatedWithImage(sound, image);
      isAssociatedWithSound = imageProvider.isAssociatedWithSound(image, sound);
    } catch (ProviderException e) {
      throw GameExceptions.UnableToSolve(FindTheSound.NAME, e);
    }

    return isAssociatedWithImage && isAssociatedWithSound;
  }

  @Override
  protected FindTheSoundResponse toResponse(FindTheSound toCreate) throws MciException {
    List<Sound> selectedSounds;
    List<Image> selectedImages;
    try {
      selectedSounds = soundProvider.selectSoundsByEntityId(toCreate.getId());
      selectedImages = imageProvider.selectImagesByEntityId(toCreate.getId());
    } catch (ProviderException e) {
      throw GameExceptions.UnableToResponse(FindTheSound.NAME, e);
    }

    if (selectedSounds.isEmpty()) {
      throw GameExceptions.UnableToResponse(FindTheSound.NAME, "No sounds associated with id: " + toCreate.getId());
    }

    if (selectedImages.isEmpty()) {
      throw GameExceptions.UnableToResponse(FindTheSound.NAME, "No images associated with id: " + toCreate.getId());
    }

    List<ImageData> images = selectedImages.stream()
      .map(x -> {
        ImageData data = new ImageData();
        data.setId(x.getId());
        data.setPath(x.getPath());
        return data;
      }).collect(Collectors.toList());

    FindTheSoundResponse response = new FindTheSoundResponse(toCreate);
    response.setSoundId(selectedSounds.get(0).getId());
    response.setSoundPath(selectedSounds.get(0).getPath());
    response.setImages(images);
    return response;
  }
}
