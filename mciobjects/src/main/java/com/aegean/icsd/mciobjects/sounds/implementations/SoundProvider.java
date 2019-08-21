package com.aegean.icsd.mciobjects.sounds.implementations;

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
}
