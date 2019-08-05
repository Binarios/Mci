package com.aegean.icsd.mciwebapp.providers.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.mciwebapp.providers.beans.ProviderException;
import com.aegean.icsd.mciwebapp.providers.beans.WordConfiguration;
import com.aegean.icsd.mciwebapp.providers.interfaces.IWordProvider;
import com.aegean.icsd.mciwebapp.providers.objects.Word;

@Service
public class WordProvider implements IWordProvider {

  @Autowired
  private WordConfiguration config;

  @Autowired
  private IGenerator generator;

  @Override
  public List<Word> getWords(int number) throws ProviderException {
    List<Word> words = new ArrayList<>();
    try (Stream<String> fileLines = Files.lines(Paths.get(config.getLocation()))) {
      long totalNb = fileLines.count();
      for (int i = 0; i < number; i++) {
        long lineNumber = ThreadLocalRandom.current().nextLong(0, totalNb);
        String line = fileLines.skip(lineNumber).findFirst().orElse(null);
        if (line != null) {
          String[] fragments = line.split(config.getDelimiter());
          String wordRaw = fragments[config.getValueIndex()];
          Word word = words.stream().filter(x -> wordRaw.equals(x.getValue())).findFirst().orElse(null);
          if (word != null) {
            i--;
          } else {
            word = new Word();
            word.setValue(wordRaw);
            words.add(word);
          }
        }
      }
    } catch (IOException e) {
      throw Exceptions.UnableToReadFile(config.getLocation(), e);
    }

    for (Word word : words) {
      try {
        generator.upsertObj(word);
      } catch (EngineException e) {
        e.printStackTrace();
      }
    }

    return words;
  }
}
