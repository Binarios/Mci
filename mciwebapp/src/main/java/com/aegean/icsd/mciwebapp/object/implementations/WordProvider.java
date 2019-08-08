package com.aegean.icsd.mciwebapp.object.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.object.beans.CharacterObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.WordConfiguration;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IProvider;

@Service
public class WordProvider implements IProvider {

  @Autowired
  private WordConfiguration config;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Override
  public List<String> getObjectsIds(int number) throws ProviderException {

    List<String> objIds = new ArrayList<>();
    if (number < 0) {
      number = 0;
    }

    EntityRules er;
    try {
      er = rules.getEntityRules(Word.NAME);
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveRules(Word.NAME, e);
    }

    List<EntityRestriction> simplifiedRestrictions = generator.calculateExactCardinality(er.getRestrictions());
    List<Word> words = readWords(number);

    for (Word word : words) {
      try {
        generator.upsertObj(word);
        objIds.add(word.getId());
        for (EntityRestriction res : simplifiedRestrictions) {
          if ("hasCharacter".equals(res.getOnProperty().getName())) {
            List<String> charsIds = createCharacters(word);
            for (String charId : charsIds) {
              generator.createObjRelation(word.getId(), res.getOnProperty(), charId);
            }
          }
        }
      } catch (EngineException e) {
        e.printStackTrace();
      }
    }

    return objIds;
  }

  List<String> createCharacters (Word word) throws ProviderException {
    List<String> charIds = new ArrayList<>();

    EntityRestriction res;
    try {
      res = rules.getEntityRestriction(CharacterObj.NAME, "hasNextCharacter");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveRules(Word.NAME, e);
    }

    char[] chars = StringUtils.reverse(word.getValue()).toCharArray();
    CharacterObj lastlyCreated = null;
    for (char ch : chars) {
      CharacterObj chObj = getCharacterFromValue(ch);
      try {
        generator.upsertObj(chObj);
        if (lastlyCreated != null) {
          generator.createObjRelation(chObj.getId(), res.getOnProperty(), lastlyCreated.getId());
        }
        charIds.add(chObj.getId());
        lastlyCreated = chObj;
      } catch (EngineException e) {
        throw Exceptions.GenerationError(e);
      }
    }

    return charIds;
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

  CharacterObj getCharacterFromValue(char value) {
    CharacterObj ch = new CharacterObj();
    ch.setValue(value);
    return ch;
  }
}
