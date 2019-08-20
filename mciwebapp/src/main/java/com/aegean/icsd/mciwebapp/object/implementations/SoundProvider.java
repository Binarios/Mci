package com.aegean.icsd.mciwebapp.object.implementations;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Sound;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.configurations.SoundConfiguration;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectFileProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.ISoundProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;

@Service
public class SoundProvider implements ISoundProvider {

  @Autowired
  private SoundConfiguration config;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private IObjectFileProvider fileProvider;

  @PostConstruct
  void readSounds() throws ProviderException {

    EntityRestriction soundSubjRes;
    try {
      soundSubjRes = rules.getEntityRestriction(Sound.NAME, "hasSubject");
    } catch (RulesException e) {
      throw ProviderExceptions.GenerationError(Sound.NAME, e);
    }

    List<String> lines = fileProvider.getLines(config.getLocation() + "/" + config.getFilename());
    for (String line : lines) {
      String[] fragments = line.split(config.getDelimiter());
      String url = fragments[config.getUrlIndex()];
      String subject = fragments[config.getSubjectIndex()];
      try {
        Sound criteria = new Sound();
        criteria.setPath(url);
        Sound sound = getOrUpsertSound(criteria);
        Word subjectWord = wordProvider.getWordWithValue(subject);
        generator.createObjRelation(sound.getId(), soundSubjRes.getOnProperty(), subjectWord.getId());
      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(Sound.NAME, e);
      }
    }
  }

  Sound getOrUpsertSound(Sound sound) throws ProviderException {
    try {
      List<Sound> results = generator.selectGameObject(sound);
      if (results.isEmpty()) {
        generator.upsertGameObject(sound);
        return sound;
      } else {
        List<Sound> found = results.stream()
          .filter(x -> x.getPath().equals(sound.getPath()))
          .collect(Collectors.toList());
        return found.get(0);
      }
    } catch (EngineException e) {
      throw ProviderExceptions.GenerationError(Sound.NAME, e);
    }
  }
}
