package com.aegean.icsd.mciwebapp.object.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.configurations.WordConfiguration;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;

@Service
public class WordProvider implements IWordProvider {

  @Autowired
  private WordConfiguration config;

  @Autowired
  private IGenerator generator;

  @Override
  public List<String> getWordsIds(int number) throws ProviderException {

    List<String> objIds = new ArrayList<>();
    if (number < 0) {
      number = 0;
    }

    List<Word> words = readWords(number);

    for (Word word : words) {
      try {
        generator.upsertObj(word);
        objIds.add(word.getId());
      } catch (EngineException e) {
        e.printStackTrace();
      }
    }

    return objIds;
  }

  List<Word> readWords(int number) throws ProviderException {
    List<Word> words = new ArrayList<>();
    if (number > 0) {
      Supplier<Stream<String>> streamSupplier = () -> {
        try {
          return Files.lines(Paths.get(config.getLocation()));
        } catch (IOException e) {
          return null;
        }
      };

      if (streamSupplier.get() == null ) {
        throw Exceptions.UnableToReadFile(config.getLocation());
      }

      long totalNb = streamSupplier.get().count();
      for (int i = 0; i < number; i++) {
        long lineNumber = ThreadLocalRandom.current().nextLong(0, totalNb);
        String line =  streamSupplier.get().skip(lineNumber).findFirst().orElse(null);
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
    }
    return words;
  }
}
