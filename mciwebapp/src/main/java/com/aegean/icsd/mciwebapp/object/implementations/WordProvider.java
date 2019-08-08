package com.aegean.icsd.mciwebapp.object.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.WordConfiguration;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectProvider;

public class WordProvider extends AbstractProvider {

  private WordConfiguration config;
  private IGenerator generator;
  private IRules rules;
  private IObjectProvider provider;
  private IAnnotationReader ano;

  @Autowired
  public WordProvider(WordConfiguration config,
                      IGenerator generator, IRules rules, IObjectProvider provider, IAnnotationReader ano ) {
    super(ano, rules, generator, provider);
    this.config = config;
  }

  @Override
  public List<String> getObjectsIds(int number) throws ProviderException {
    return getObjectIds(number, null);
  }

  @Override
  public List<String> getObjectsIds(List<String> values) throws ProviderException {
    return getObjectIds(-1, values);
  }

  List<String> getObjectIds(int number, List<String> values) throws ProviderException {
    if (number < 0) {
      number = 0;
    }
    List<Word> words;
    if (values != null
      && values.size() > 0) {
      words = getWordsFromValues(values);
    } else {
      words = readWords(number);
    }

    return generateObjects(words);
  }

  @Override
  protected Map<EntityProperty, List<String>> handleRestrictions(Object forObject, List<EntityRestriction> restrictions)
    throws ProviderException {
    Word word = (Word) forObject;
    char[] characters = word.getValue().toCharArray();
    List<String> chars = new LinkedList<>();
    for (char c : characters) {
      chars.add("" + c);
    }
    Map<EntityProperty, List<String>> relations = new HashMap<>();
    for (EntityRestriction res : restrictions) {
      if (res.getOnProperty().isObjectProperty()) {
        List<String> objIds = provider.getObjectsIds(res.getOnProperty().getRange(), chars);
        relations.put(res.getOnProperty(), objIds);
      }
    }
    return relations;
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

  List<Word> getWordsFromValues(List<String> values) {
    List<Word> words = new ArrayList<>();
    for (String value : values) {
      words.add(getWordFromValue(value));
    }
    return words;
  }

  Word getWordFromValue(String value) {
    Word word = new Word();
    word.setValue(value);
    return word;
  }


}
