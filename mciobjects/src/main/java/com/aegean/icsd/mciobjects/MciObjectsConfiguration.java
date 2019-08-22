package com.aegean.icsd.mciobjects;

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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.aegean.icsd.mciobjects.images.configuration.ImageConfiguration;
import com.aegean.icsd.mciobjects.questions.configurations.QuestionConfiguration;
import com.aegean.icsd.mciobjects.sounds.configurations.SoundConfiguration;
import com.aegean.icsd.mciobjects.words.configuration.WordConfiguration;

@Configuration
@ComponentScan({"com.aegean.icsd.mciobjects"})
@PropertySources({
  @PropertySource("classpath:com/aegean/icsd/mciobjects/providers/words.properties"),
  @PropertySource("classpath:com/aegean/icsd/mciobjects/providers/images.properties"),
  @PropertySource("classpath:com/aegean/icsd/mciobjects/providers/sounds.properties")
})
public class MciObjectsConfiguration {
  private static final Logger LOGGER = LogManager.getLogger(MciObjectsConfiguration.class);

  @Autowired
  private Environment env;

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
