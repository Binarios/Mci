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
import com.aegean.icsd.mciwebapp.object.WordConfiguration;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;

@Service
public class WordProvider implements IWordProvider {

  @Autowired
  private WordConfiguration config;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Override
  public List<Word> getWords(int number) throws ProviderException {
    List<Word> words = readWords(number);
    for (Word word : words) {
      try {
        generator.upsertObj(word);
      } catch (EngineException e) {
        throw Exceptions.GenerationError(e);
      }

      EntityRules er;
      try {
        er = rules.getEntityRules(Word.NAME);
      } catch (RulesException e) {
        throw Exceptions.UnableToRetrieveRules(Word.NAME, e);
      }

    }
    return words;
  }

  List<Word> readWords(int number) throws ProviderException {
    List<Word> words = new ArrayList<>();
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

    return words;
  }
}
