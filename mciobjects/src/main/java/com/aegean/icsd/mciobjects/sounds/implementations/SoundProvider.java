package com.aegean.icsd.mciobjects.sounds.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
import com.aegean.icsd.mciobjects.sounds.beans.Sound;
import com.aegean.icsd.mciobjects.sounds.configurations.SoundConfiguration;
import com.aegean.icsd.mciobjects.sounds.interfaces.ISoundProvider;
import com.aegean.icsd.mciobjects.words.beans.Word;

@Service
public class SoundProvider implements ISoundProvider {

  @Autowired
  private SoundConfiguration config;

  @Autowired
  private IObjectsDao dao;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Override
  public List<Sound> getNewSoundsFor(String entityName, int count, Sound criteria) throws ProviderException {
    List<String> availableIds = dao.getNewObjectIdsFor(entityName, Sound.class);
    if (availableIds.isEmpty()) {
      throw ProviderExceptions.UnableToGenerateObject(Sound.NAME);
    }
    List<Sound> availableSounds = new ArrayList<>();
    Collections.shuffle(availableIds, new Random(System.currentTimeMillis()));
    for (String id : availableIds) {
      Sound cp = copy(criteria);
      cp.setId(id);
      try {
        List<Sound> results = generator.selectGameObject(cp);
        if (!results.isEmpty()) {
          availableSounds.add(results.get(0));
        }
      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(Sound.NAME, e);
      }
      if(availableSounds.size() == count) {
        break;
      }
    }

    if(availableSounds.size() != count) {
      throw ProviderExceptions.UnableToGetObject(String.format("Unable to find %s new sounds for %s", count, entityName));
    }

    return availableSounds;
  }

  @Override
  public List<Sound> selectSoundsByEntityId(String entityId) throws ProviderException {
    List<String> ids = dao.getAssociatedObjectsOfEntityId(entityId, Sound.class);
    List<Sound> sounds = new ArrayList<>();
    for (String id : ids) {
      Sound sound = new Sound();
      sound.setId(id);
      try {
        List<Sound> results = generator.selectGameObject(sound);
        sounds.add(results.get(0));
      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetObject(Sound.NAME + " for entityId = " + entityId, e);
      }
    }
    return sounds;
  }

  @Override
  public boolean isAssociatedWithImage(Sound sound, Image image) throws ProviderException {
    EntityRestriction hasAssociatedImageRes;
    try {
      hasAssociatedImageRes = rules.getEntityRestriction("SoundImage", "hasAssociatedImage");
    } catch (RulesException e) {
      throw ProviderExceptions.GenerationError(Sound.NAME, e);
    }
    return dao.areObjectsAssociatedOn(sound, image, hasAssociatedImageRes.getOnProperty());
  }

  @Override
  public Sound selectRandomSoundWithSubject(Word word) throws ProviderException {
    EntityRestriction imageSubjRes;
    try {
      imageSubjRes = rules.getEntityRestriction(Sound.NAME, "hasSubject");
    } catch (RulesException e) {
      throw ProviderExceptions.UnableToGetObject(Sound.NAME, e);
    }

    List<String> ids = dao.getIdAssociatedWithOtherOnProperty(Sound.NAME, Word.NAME, word.getId(), imageSubjRes.getOnProperty());
    if (ids.isEmpty()) {
      return null;
    }

    Collections.shuffle(ids,new Random(System.currentTimeMillis()));
    String id = ids.get(0);

    Sound criteria = new Sound();
    criteria.setId(id);
    try {
      List<Sound> results = generator.selectGameObject(criteria);
      Sound sound = null;
      if (!results.isEmpty()) {
        sound = results.get(0);
      }
      return sound;
    } catch (EngineException e) {
      throw ProviderExceptions.UnableToGetObject(Sound.NAME, e);
    }
  }

  Sound copy(Sound toCopy) {
    Sound cp = new Sound();
    cp.setId(toCopy.getId());
    cp.setPath(toCopy.getPath());
    cp.setImageAssociated(toCopy.isImageAssociated());
    return cp;
  }
}
