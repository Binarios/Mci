package com.aegean.icsd.mciwebapp;

import java.util.Collections;
import java.util.List;

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

import com.aegean.icsd.mciwebapp.object.configurations.WordConfiguration;

import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

@Configuration
@EnableWebMvc
@ComponentScan({"com.aegean.icsd.engine", "com.aegean.icsd.mciwebapp"})
@PropertySources(
  @PropertySource("classpath:com/aegean/icsd/mciwebapp/providers/word.properties")
)
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
    config.setLocation(env.getProperty("loc"));
    config.setDelimiter(env.getProperty("delimiter"));
    int valueIndex = 1;
    if (env.getProperty("valueIndex") != null) {
      valueIndex = Integer.parseInt(env.getProperty("valueIndex"));
    }
    config.setValueIndex(valueIndex);
    return config;
  }
}
