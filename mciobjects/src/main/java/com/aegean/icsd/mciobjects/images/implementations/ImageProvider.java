package com.aegean.icsd.mciobjects.images.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.common.daos.IObjectsDao;
import com.aegean.icsd.mciobjects.common.implementations.ProviderExceptions;
import com.aegean.icsd.mciobjects.images.beans.Image;
import com.aegean.icsd.mciobjects.images.beans.ImageData;
import com.aegean.icsd.mciobjects.images.daos.IImageDao;
import com.aegean.icsd.mciobjects.images.interfaces.IImageProvider;
import com.aegean.icsd.mciobjects.sounds.beans.Sound;
import com.aegean.icsd.mciobjects.words.beans.Word;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;

@Service
public class ImageProvider implements IImageProvider {

  private static Logger LOGGER = Logger.getLogger(ImageProvider.class);

  @Autowired
  private IGenerator generator;

  @Autowired
  private IObjectsDao dao;

  @Autowired
  private IImageDao imageDao;

  @Autowired
  private IMciModelReader model;

  @Autowired
  private Map<String, EntityRestriction> imageRules;

  @Override
  public List<String> getImageIds() throws ProviderException {
    return dao.getObjectIds(Image.class);
  }

  @Override
  public List<Image> getNewImagesFor(String entityName, int count, Image criteria) throws ProviderException {
    List<String> availableIds = dao.getNewObjectIdsFor(entityName, Image.class);
    if (availableIds.isEmpty()) {
      throw ProviderExceptions.UnableToGenerateObject(Image.NAME);
    }
    List<Image> availableImages = new ArrayList<>();
    Collections.shuffle(availableIds, new Random(System.currentTimeMillis()));
    for (String id : availableIds) {
      Image cp = copy(criteria);
      cp.setId(id);
      try {
        List<Image> results = generator.selectGameObject(cp);
        if (!results.isEmpty()) {
          availableImages.add(results.get(0));
        }
      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(Image.NAME, e);
      }
      if(availableImages.size() == count) {
        break;
      }
    }

    if(availableImages.size() != count) {
      throw ProviderExceptions.UnableToGetObject(String.format("Unable to find %s new images for %s", count, entityName));
    }
    return availableImages;
  }

  @Override
  public List<Image> getNewOrderedImagesFor(String entityName, int cardinality) throws ProviderException {
    List<Image> images = new ArrayList<>();

    Map<String, List<String>> orderedImages = imageDao.getOrderedImages(cardinality);
    for (Map.Entry<String, List<String>> entry : orderedImages.entrySet()) {
      String rootId = entry.getKey();
      boolean exists = imageDao.rootOrderImageExistsFor(rootId, entityName);
      if (exists) {
        continue;
      }
      Image criteria = new Image();
      criteria.setId(rootId);
      try {
        Image orderedImage = generator.selectGameObject(criteria).get(0);
        images.add(orderedImage);
        for (String nextImageId : entry.getValue()) {
          criteria.setId(nextImageId);
          Image next = generator.selectGameObject(criteria).get(0);
          images.add(next);
        }

      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetObject(Image.NAME, e);
      }
      if (images.size() == cardinality) {
        break;
      }
    }

    if (images.isEmpty()) {
      throw ProviderExceptions.UnableToGetObject("Unable to retrieve new ordered Images for this level");
    }
    return images;
  }

  @Override
  public Image[][] getImageChunks(Image image, int rows, int cols) throws ProviderException {
    Image[][] imageChunks = new ImageUtils().splitImage(image, rows, cols);
    for (Image[] imageRow : imageChunks) {
      for (Image value : imageRow) {
        try {
          generator.upsertGameObject(value);
        } catch (EngineException e) {
          throw ProviderExceptions.UnableToGetObject(Image.NAME, e);
        }
      }
    }
    return imageChunks;
  }

  @Override
  public List<Image> selectNewImagesForEntity(String entityName, int count) throws ProviderException {
    List<Image> images = new ArrayList<>();
    try {
      List<String> imageIds = dao.getNewObjectIdsFor(entityName, Image.class);
      for(String imageId : imageIds) {
        if (images.size() == count) {
          break;
        }
        Image criteria = new Image();
        criteria.setId(imageId);
        List<Image> result = generator.selectGameObject(criteria);
        if (!result.isEmpty()) {
          images.addAll(result);
        }
      }
    } catch (ProviderException | EngineException e) {
      throw ProviderExceptions.UnableToGetObject(Image.NAME, e);
    }

    return images;
  }

  @Override
  public Image selectRandomImageWithSubject(Word word) throws ProviderException {
    EntityRestriction imageSubjRes = imageRules.get("hasSubject");

    List<String> ids = dao.getIdAssociatedWithOtherOnProperty(Image.NAME, Word.NAME, word.getId(), imageSubjRes.getOnProperty());
    if (ids.isEmpty()) {
      return null;
    }

    Collections.shuffle(ids,new Random(System.currentTimeMillis()));
    String id = ids.get(0);

    Image criteria = new Image();
    criteria.setId(id);

    try {
      Image image = null;
      List<Image> results = generator.selectGameObject(criteria);
      if (!results.isEmpty()) {
        image = results.get(0);
      }
      return image;
    } catch (EngineException e) {
      throw ProviderExceptions.UnableToGetObject(Image.NAME, e);
    }
  }

  @Override
  public boolean isSolutionCorrect(List<ImageData> solution) throws ProviderException {
    solution.sort(Comparator.comparing(ImageData::getOrder));
    return imageDao.isOrderCorrect(solution);
  }

  @Override
  public boolean isAssociatedWithSound(Image image, Sound sound) throws ProviderException {
    EntityRestriction hasAssociatedSound = imageRules.get("hasAssociatedSound");
    return dao.areObjectsAssociatedOn(image, sound, hasAssociatedSound.getOnProperty());
  }

  @Override
  public Image selectImageByNode(String nodeName) throws ProviderException {
    String id = model.removePrefix(nodeName);
    Image criteria = new Image();
    criteria.setId(id);
    try {
      List<Image> images = generator.selectGameObject(criteria);
      if (images.size() != 1) {
        throw ProviderExceptions.UnableToGetObject("No image or more than one image found with the same id :" + id);
      }
      return images.get(0);
    } catch (EngineException e) {
      throw ProviderExceptions.UnableToGetObject("No image or more than one image found with the same id :" + id, e);
    }
  }

  @Override
  public List<Image> selectImagesByEntityId(String entityId) throws ProviderException {
    List<String> ids = dao.getAssociatedObjectsOfEntityId(entityId, Image.class);
    List<Image> images = new ArrayList<>();
    for (String id : ids) {
      Image criteria = new Image();
      criteria.setId(id);
      try {
        List<Image> results = generator.selectGameObject(criteria);
        if (results.size() != 1) {
          throw ProviderExceptions.UnableToGetObject("No image or more than one image found with the same id :" + id);
        }
        images.add(results.get(0));
      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetObject("No image or more than one image found with the same id :" + id, e);
      }
    }
    return images;
  }

  @Override
  public String selectAssociatedSubject(String imageId) throws ProviderException {
    EntityRestriction imageSubjRes = imageRules.get("hasSubject");
    List<String> wordId = dao.getAssociatedIdsOnPropertyForEntityId(imageId, imageSubjRes.getOnProperty(), Word.class);
    return wordId.get(0);
  }

  Image copy(Image toCopy) {
    Image cp = new Image();
    cp.setId(toCopy.getId());
    cp.setPath(toCopy.getPath());
    cp.setSoundAssociated(toCopy.isSoundAssociated());
    return cp;
  }
}
