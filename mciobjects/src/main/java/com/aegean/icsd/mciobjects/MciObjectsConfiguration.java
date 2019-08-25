package com.aegean.icsd.mciobjects;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.blocks.beans.BlockSet;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.common.implementations.ProviderExceptions;
import com.aegean.icsd.mciobjects.images.beans.Image;
import com.aegean.icsd.mciobjects.images.configuration.ImageConfiguration;
import com.aegean.icsd.mciobjects.pieces.beans.Piece;
import com.aegean.icsd.mciobjects.questions.beans.Question;
import com.aegean.icsd.mciobjects.questions.configurations.QuestionConfiguration;
import com.aegean.icsd.mciobjects.sounds.beans.Sound;
import com.aegean.icsd.mciobjects.sounds.configurations.SoundConfiguration;
import com.aegean.icsd.mciobjects.words.configuration.WordConfiguration;

@Configuration
@ComponentScan({"com.aegean.icsd.mciobjects"})
@PropertySources({
  @PropertySource("classpath:com/aegean/icsd/mciobjects/providers/words.properties"),
  @PropertySource("classpath:com/aegean/icsd/mciobjects/providers/images.properties"),
  @PropertySource("classpath:com/aegean/icsd/mciobjects/providers/sounds.properties"),
  @PropertySource("classpath:com/aegean/icsd/mciobjects/providers/questions.properties")
})
public class MciObjectsConfiguration {
  private static final Logger LOGGER = LogManager.getLogger(MciObjectsConfiguration.class);

  @Autowired
  private Environment env;

  @Autowired
  private IRules rules;


  @Bean(name = "imageRules")
  public Map<String, EntityRestriction> getImageRules() throws ProviderException {
    Map<String, EntityRestriction> imageRules = new HashMap<>();
    EntityRestriction hasSubject;
    EntityRestriction hasAssociatedSound;
    try {
      hasSubject = rules.getEntityRestriction(Image.NAME, "hasSubject");
      hasAssociatedSound = rules.getEntityRestriction("ImageSound", "hasAssociatedSound");
    } catch (RulesException e) {
      throw ProviderExceptions.UnableToGetObject(Image.NAME, e);
    }
    imageRules.put("hasSubject", hasSubject);
    imageRules.put("hasAssociatedSound", hasAssociatedSound);

    return imageRules;
  }

  @Bean(name = "soundRules")
  public Map<String, EntityProperty> getSoundRules() throws ProviderException {
    Map<String, EntityProperty> soundRules = new HashMap<>();
    EntityRestriction hasSubject;
    EntityProperty hasAssociatedImage;
    try {
      hasSubject = rules.getEntityRestriction(Sound.NAME, "hasSubject");
      hasAssociatedImage = rules.getProperty(Sound.NAME, "hasAssociatedImage");
    } catch (RulesException e) {
      throw ProviderExceptions.GenerationError(Sound.NAME, e);
    }
    soundRules.put("hasAssociatedImage", hasAssociatedImage);
    soundRules.put("hasSubject", hasSubject.getOnProperty());

    return soundRules;
  }

  @Bean(name = "questionRules")
  public Map<String, EntityRestriction> getQuestionRules() throws ProviderException {
    Map<String, EntityRestriction> questionRules = new HashMap<>();
    EntityRestriction hasImage;
    EntityRestriction hasCategory;
    try {
      hasImage = rules.getEntityRestriction(Question.NAME, "hasImage");
      hasCategory = rules.getEntityRestriction(Question.NAME, "hasCategory");
    } catch (RulesException e) {
      throw ProviderExceptions.UnableToRetrieveRules(Question.NAME, e);
    }

    questionRules.put("hasCategory", hasCategory);
    questionRules.put("hasImage", hasImage);
    return questionRules;
  }

  @Bean(name = "blockRules")
  public Map<String, EntityRestriction> getBlockRules() throws ProviderException {
    Map<String, EntityRestriction> blockRules = new HashMap<>();
    EntityRestriction hasMovingBlock;
    EntityRestriction hasPreviousBlockSet;
    EntityRestriction hasBlock;
    try {
      hasMovingBlock = rules.getEntityRestriction(BlockSet.NAME, "hasMovingBlock");
      hasPreviousBlockSet = rules.getEntityRestriction(BlockSet.NAME, "hasPreviousBlockSet");
      hasBlock = rules.getEntityRestriction(BlockSet.NAME, "hasBlock");
    } catch (RulesException e) {
      throw ProviderExceptions.UnableToRetrieveRules(BlockSet.NAME, e);
    }

    blockRules.put("hasMovingBlock", hasMovingBlock);
    blockRules.put("hasPreviousBlockSet", hasPreviousBlockSet);
    blockRules.put("hasBlock", hasBlock);

    return blockRules;
  }

  @Bean(name = "pieceRules")
  public Map<String, EntityRestriction> getPieceRules() throws ProviderException {
    Map<String, EntityRestriction> pieceRules = new HashMap<>();
    EntityRestriction hasConnectingPiece;
    EntityRestriction hasImage;

    try {
      hasConnectingPiece = rules.getEntityRestriction(Piece.NAME, "hasConnectingPiece");
      hasImage = rules.getEntityRestriction(Piece.NAME, "hasImage");
    } catch (RulesException e) {
      throw ProviderExceptions.UnableToRetrieveRules(Piece.NAME, e);
    }
    pieceRules.put("hasConnectingPiece", hasConnectingPiece);
    pieceRules.put("hasImage", hasImage);
    return pieceRules;
  }

  @Bean(name = "initializerRules")
  public Map<String, EntityRestriction> getInitializerRules() throws ProviderException {
    Map<String, EntityRestriction> initializerRules = new HashMap<>();
    EntityRestriction synonymRes;
    EntityRestriction antonymRes;
    EntityRestriction soundSubjRes;
    EntityRestriction imageSubjRes;
    EntityRestriction imageTitleRes;
    EntityRestriction hasPreviousImage;
    EntityRestriction hasAssociatedSound;
    try {
      synonymRes = rules.getEntityRestriction("SynonymWord", "hasSynonym");
      antonymRes = rules.getEntityRestriction("AntonymWord", "hasAntonym");
      soundSubjRes = rules.getEntityRestriction(Sound.NAME, "hasSubject");
      imageSubjRes = rules.getEntityRestriction(Image.NAME, "hasSubject");
      imageTitleRes = rules.getEntityRestriction(Image.NAME, "hasTitle");
      hasAssociatedSound = rules.getEntityRestriction("ImageSound", "hasAssociatedSound");
      hasPreviousImage = rules.getEntityRestriction("OrderedImage", "hasPreviousImage");
    } catch (RulesException e) {
      throw ProviderExceptions.UnableToRetrieveRules(BlockSet.NAME, e);
    }

    initializerRules.put("synonymRes", synonymRes);
    initializerRules.put("antonymRes", antonymRes);
    initializerRules.put("soundSubjRes", soundSubjRes);
    initializerRules.put("imageSubjRes", imageSubjRes);
    initializerRules.put("imageTitleRes", imageTitleRes);
    initializerRules.put("hasPreviousImage", hasPreviousImage);
    initializerRules.put("hasAssociatedSound", hasAssociatedSound);

    return initializerRules;
  }

  @Bean
  public WordConfiguration getWordConfiguration() {
    WordConfiguration config = new WordConfiguration();
    config.setLocation(getPropertyValue("word.loc"));
    config.setFilename(getPropertyValue("word.filename"));
    config.setDelimiter(getPropertyValue("word.delimiter"));
    config.setValueIndex(Integer.parseInt(getPropertyValue("word.valueIndex")));
    config.setSynonymIndex(Integer.parseInt(getPropertyValue("word.synonymIndex")));
    config.setAntonymIndex(Integer.parseInt(getPropertyValue("word.antonymIndex")));
    config.setSynonymDelimiter(getPropertyValue("word.synonym.delimiter"));
    config.setAntonymDelimiter(getPropertyValue("word.antonym.delimiter"));
    return config;
  }

  @Bean
  public ImageConfiguration getImageConfiguration() {
    ImageConfiguration config = new ImageConfiguration();
    config.setLocation(getPropertyValue("image.loc"));
    config.setFilename(getPropertyValue("image.filename"));
    config.setDelimiter(getPropertyValue("image.delimiter"));
    config.setUrlIndex(Integer.parseInt(getPropertyValue("image.index.url")));
    config.setTitleIndex(Integer.parseInt(getPropertyValue("image.index.title")));
    config.setSubjectIndex(Integer.parseInt(getPropertyValue("image.index.subject")));
    config.setParentIndex(Integer.parseInt(getPropertyValue("image.index.parentImage")));
    return config;
  }

  @Bean
  public SoundConfiguration getSoundConfiguration() {
    SoundConfiguration config = new SoundConfiguration();
    config.setLocation(getPropertyValue("sound.loc"));
    config.setFilename(getPropertyValue("sound.filename"));
    config.setDelimiter(getPropertyValue("sound.delimiter"));
    config.setUrlIndex(Integer.parseInt(getPropertyValue("sound.index.url")));
    config.setSubjectIndex(Integer.parseInt(getPropertyValue("sound.index.subject")));
    return config;
  }

  @Bean
  public QuestionConfiguration getQuestionConfiguration() {
    QuestionConfiguration config = new QuestionConfiguration();
    config.setLocation(getPropertyValue("question.loc"));
    config.setFilename(getPropertyValue("question.filename"));
    config.setDelimiter(getPropertyValue("question.delimiter"));
    config.setChoicesDelimiter(getPropertyValue("question.choicesDelimiter"));
    config.setQuestionIndex(Integer.parseInt(getPropertyValue("question.questionIndex")));
    config.setCorrectAnswerIndex(Integer.parseInt(getPropertyValue("question.correctAnswerIndex")));
    config.setChoicesIndex(Integer.parseInt(getPropertyValue("question.choicesIndex")));
    config.setCategoryIndex(Integer.parseInt(getPropertyValue("question.categoryIndex")));
    config.setDifficultyIndex(Integer.parseInt(getPropertyValue("question.difficultyIndex")));
    return config;
  }


  private String getPropertyValue (String propertyName) {
    String value = env.getProperty(propertyName);
    if (StringUtils.isEmpty(value)) {
      throw new IllegalArgumentException(String.format("Property %s not found in configuration", propertyName));
    }
    return value;
  }
}
