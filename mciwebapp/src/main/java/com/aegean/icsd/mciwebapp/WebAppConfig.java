package com.aegean.icsd.mciwebapp;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.aegean.icsd.mciwebapp.object.configurations.ImageConfiguration;
import com.aegean.icsd.mciwebapp.object.configurations.WordConfiguration;

import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

@Configuration
@EnableWebMvc
@ComponentScan({"com.aegean.icsd.engine", "com.aegean.icsd.mciwebapp"})
@PropertySources({
  @PropertySource("classpath:com/aegean/icsd/mciwebapp/providers/words.properties"),
  @PropertySource("classpath:com/aegean/icsd/mciwebapp/providers/images.properties")
})
public class WebAppConfig implements WebMvcConfigurer {

  @Autowired
  private Environment env;

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
     configurer.enable();
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    GsonBuilder b = new GsonBuilder();
    b.registerTypeAdapterFactory(DateTypeAdapter.FACTORY);

    GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
    gsonHttpMessageConverter.setGson(b.create());
    gsonHttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
    converters.add(gsonHttpMessageConverter);
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

  private String getPropertyValue (String propertyName) {
    String value = env.getProperty(propertyName);
    if (StringUtils.isEmpty(value)) {
      throw new IllegalArgumentException(String.format("Property %s not found in configuration", propertyName));
    }
    return value;
  }
}
