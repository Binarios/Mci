package com.aegean.icsd.mciobjects;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.common.implementations.ProviderExceptions;
import com.aegean.icsd.mciobjects.common.interfaces.IObjectFileProvider;
import com.aegean.icsd.mciobjects.images.beans.Image;
import com.aegean.icsd.mciobjects.images.configuration.ImageConfiguration;
import com.aegean.icsd.mciobjects.images.interfaces.IImageProvider;
import com.aegean.icsd.mciobjects.questions.configurations.QuestionConfiguration;
import com.aegean.icsd.mciobjects.sounds.beans.Sound;
import com.aegean.icsd.mciobjects.sounds.configurations.SoundConfiguration;
import com.aegean.icsd.mciobjects.sounds.interfaces.ISoundProvider;
import com.aegean.icsd.mciobjects.words.beans.Word;
import com.aegean.icsd.mciobjects.words.configuration.WordConfiguration;
import com.aegean.icsd.mciobjects.words.interfaces.IWordProvider;

@Component
public class ObjectsInitiator {

  @Autowired
  private WordConfiguration wordConfig;

  @Autowired
  private ImageConfiguration imageConfig;

  @Autowired
  private SoundConfiguration soundConfig;

  @Autowired
  private QuestionConfiguration questionConfig;

  @Autowired
  private Map<String, EntityRestriction> initializerRules;

  @Autowired
  private IRules rules;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IObjectFileProvider fileProvider;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private ISoundProvider soundProvider;

  @Autowired
  private IImageProvider imageProvider;

  void setupObjects() throws ProviderException {
    EntityRestriction synonymRes = initializerRules.get("synonymRes");
    EntityRestriction antonymRes = initializerRules.get("antonymRes");
    EntityRestriction soundSubjRes = initializerRules.get("soundSubjRes");
    EntityRestriction imageSubjRes = initializerRules.get("imageSubjRes");
    EntityRestriction imageTitleRes = initializerRules.get("imageTitleRes");
    EntityRestriction hasPreviousImage = initializerRules.get("hasPreviousImage");
    EntityRestriction hasAssociatedSound = initializerRules.get("hasAssociatedSound");
    EntityProperty hasAssociatedImage;
    try {
      hasAssociatedImage = rules.getProperty(Sound.NAME, "hasAssociatedImage");
    } catch (RulesException e) {
      throw ProviderExceptions.UnableToRetrieveRules("SynonymWord", e);
    }

    setupWords(synonymRes, antonymRes);
    setupSounds(soundSubjRes, hasAssociatedImage);
    setupImages(imageSubjRes, imageTitleRes, hasPreviousImage, hasAssociatedSound);
  }

  void setupWords(EntityRestriction synonymRes, EntityRestriction antonymRes) throws ProviderException {
    List<String> lines = fileProvider.getLines(wordConfig.getLocation() + "/" + wordConfig.getFilename());
    for (String line : lines) {
      String[] fragments = line.split(wordConfig.getDelimiter());
      String valueRaw = fragments[wordConfig.getValueIndex()];

      if (fragments.length == 1) {
        continue;
      }

      String antonymRaw = fragments[wordConfig.getAntonymIndex()];
      String synonymRaw = fragments[wordConfig.getSynonymIndex()];

      Word value = new Word();
      value.setValue(valueRaw);
      value.setLength(valueRaw.length());
      value = getOrUpsertGameObject(value);

      if (!StringUtils.isEmpty(antonymRaw)) {
        String[] antonymsRaw = antonymRaw.split(wordConfig.getAntonymDelimiter());
        handleAntonyms(value, antonymRes, antonymsRaw);
      }

      if (!StringUtils.isEmpty(synonymRaw)) {
        String[] synonymsRaw = synonymRaw.split(wordConfig.getSynonymDelimiter());
        handleSynonyms(value, synonymRes, synonymsRaw);
      }
    }
  }

  void setupSounds(EntityRestriction soundSubjRes, EntityProperty hasAssociatedImage ) throws ProviderException {

    List<String> lines = fileProvider.getLines(soundConfig.getLocation() + "/" + soundConfig.getFilename());
    for (String line : lines) {
      String[] fragments = line.split(soundConfig.getDelimiter());
      String url = fragments[soundConfig.getUrlIndex()];
      String subject = fragments[soundConfig.getSubjectIndex()];
      try {
        Sound criteria = new Sound();
        criteria.setPath(url);
        Sound sound = getOrUpsertGameObject(criteria);
        Word subjectWord = wordProvider.getWordWithValue(subject);
        generator.createObjRelation(sound.getId(), soundSubjRes.getOnProperty(), subjectWord.getId());

        Image image = imageProvider.selectRandomImageWithSubject(subjectWord);

        if (image != null && !StringUtils.isEmpty(image.getId())) {
          image.setSoundAssociated(true);
          generator.upsertGameObject(image);
          sound.setImageAssociated(true);
          generator.upsertGameObject(sound);
          generator.createObjRelation(sound.getId(), hasAssociatedImage, image.getId());
        }

      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(Sound.NAME, e);
      }
    }
  }

  void setupImages(EntityRestriction imageSubjRes,
                   EntityRestriction imageTitleRes,
                   EntityRestriction hasPreviousImage,
                   EntityRestriction hasAssociatedSound) throws ProviderException {

    List<String> lines = fileProvider.getLines(imageConfig.getLocation() + "/" + imageConfig.getFilename());
    for (String line : lines) {
      Image criteria = new Image();
      Image parentImage = null;

      String[] currentFragments = line.split(imageConfig.getDelimiter());
      String currentUrl = currentFragments[imageConfig.getUrlIndex()];
      String currentTitle = currentFragments[imageConfig.getTitleIndex()];
      String currentSubject = currentFragments[imageConfig.getSubjectIndex()];

      if (currentFragments.length > imageConfig.getParentIndex()) {
        String currentParentImage = currentFragments[imageConfig.getParentIndex()];
        Image parentCriteria = new Image();
        parentCriteria.setPath(currentParentImage);
        parentImage = getOrUpsertGameObject(parentCriteria);
      }

      try {
        criteria.setOrdered(parentImage != null && !StringUtils.isEmpty(parentImage.getId()));
        criteria.setPath(currentUrl);
        Image image = getOrUpsertGameObject(criteria);
        Word titleWord = wordProvider.getWordWithValue(currentTitle);
        Word subjectWord = wordProvider.getWordWithValue(currentSubject);
        Sound sound = soundProvider.selectRandomSoundWithSubject(subjectWord);
        generator.createObjRelation(image.getId(), imageTitleRes.getOnProperty(), titleWord.getId());
        generator.createObjRelation(image.getId(), imageSubjRes.getOnProperty(), subjectWord.getId());
        if (image.isOrdered()) {
          generator.createObjRelation(image.getId(), hasPreviousImage.getOnProperty(), parentImage.getId());
        }
        if (sound != null && !StringUtils.isEmpty(sound.getId())) {
          image.setSoundAssociated(true);
          generator.upsertGameObject(image);
          sound.setImageAssociated(true);
          generator.upsertGameObject(sound);
          generator.createObjRelation(image.getId(), hasAssociatedSound.getOnProperty(), sound.getId());
        }
      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(Image.NAME, e);
      }
    }
  }

  void handleAntonyms(Word value, EntityRestriction antonymRes, String... antonyms) throws ProviderException {

    for (String antonym : antonyms) {
      Word antonymWord = new Word();
      antonymWord.setValue(antonym);
      antonymWord.setLength(antonym.length());
      antonymWord.setAntonym(true);
      getOrUpsertGameObject(antonymWord);
      if (antonymWord.getId() != null) {
        try {
          generator.createObjRelation(value.getId(), antonymRes.getOnProperty(), antonymWord.getId());
          if (value.isAntonym() == null || !value.isAntonym()) {
            value.setAntonym(true);
            generator.upsertGameObject(value);
          }
        } catch (EngineException e) {
          throw ProviderExceptions.GenerationError(Word.NAME, e);
        }
      }
    }
  }

  void handleSynonyms(Word value, EntityRestriction synonymRes, String... synonyms) throws ProviderException {
    for (String synonym : synonyms) {
      Word synonymWord = new Word();
      synonymWord.setValue(synonym);
      synonymWord.setLength(synonym.length());
      synonymWord.setSynonym(true);
      synonymWord = getOrUpsertGameObject(synonymWord);
      if (synonymWord.getId() != null) {
        try {
          generator.createObjRelation(value.getId(), synonymRes.getOnProperty(), synonymWord.getId());
          if (value.isSynonym() == null || !value.isSynonym()) {
            value.setSynonym(true);
            generator.upsertGameObject(value);
          }
        } catch (EngineException e) {
          throw ProviderExceptions.GenerationError(Word.NAME, e);
        }
      }
    }
  }

  <T extends BaseGameObject> T getOrUpsertGameObject(T gameObject) throws ProviderException {
    try {
      List<T> results = generator.selectGameObject(gameObject);
      if (results.isEmpty()) {
        generator.upsertGameObject(gameObject);
        return gameObject;
      } else {
        return results.get(0);
      }
    } catch (EngineException e) {
      throw ProviderExceptions.GenerationError(Word.NAME, e);
    }
  }

}
