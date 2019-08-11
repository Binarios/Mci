package com.aegean.icsd.mciwebapp.object.implementations;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.configurations.WordConfiguration;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectFileProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;

@Service
public class WordProvider implements IWordProvider {

  private static Logger LOGGER = Logger.getLogger(WordProvider.class);

  @Autowired
  private WordConfiguration config;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IObjectFileProvider fileProvider;

  @Override
  public List<String> getWordsIds(int number) throws ProviderException {
    LOGGER.info(String.format("Requested %s Words", number));
    List<String> objIds = new ArrayList<>();
    if (number < 0) {
      number = 0;
    }
    List<String> words = readWords(number);
    for (String word : words) {
      String id = getWordFromValue(word);
      objIds.add(id);
    }
    return objIds;
  }

  @Override
  public String getWordFromValue(String value) throws ProviderException {
    LOGGER.info(String.format("Requested word with value %s", value));
    Word word = toWord(value);
    try {
      String id = generator.selectObjectId(word);
      if (id == null) {
        generator.upsertObj(word);
      } else {
        word.setId(id);
      }
      return word.getId();
    } catch (EngineException e) {
      throw Exceptions.GenerationError(e);
    }
  }



  List<String> readWords(int number) throws ProviderException {
    List<String> words = new ArrayList<>();
    if (number > 0) {
      for (int i = 0; i < number; i++) {
        String line = fileProvider.getFileLineFromUrl(config.getLocation() + "/" + config.getFilename());
        String[] fragments = line.split(config.getDelimiter());
        String wordRaw = fragments[config.getValueIndex()];
        if (words.contains(wordRaw)) {
          i--;
        } else {
          words.add(wordRaw);
        }
      }
    }
    return words;
  }

  Word toWord(String value) {
    Word word = new Word();
    word.setValue(value);
    return word;
  }
}
