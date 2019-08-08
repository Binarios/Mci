package com.aegean.icsd.mciwebapp.object.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.object.beans.CharacterObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectProvider;

public class CharacterProvider extends AbstractProvider {

  private IGenerator generator;
  private IRules rules;
  private IObjectProvider provider;
  private IAnnotationReader ano;

  @Autowired
  public CharacterProvider(IGenerator generator, IRules rules, IObjectProvider provider, IAnnotationReader ano ) {
    super(ano, rules, generator, provider);
  }

  @Override
  public List<String> getObjectsIds(int number) throws ProviderException {
    return getObjectIds(null);
  }

  @Override
  public List<String> getObjectsIds(List<String> values) throws ProviderException {
    return getObjectIds(values);
  }

  List<String> getObjectIds(List<String> values) throws ProviderException {

    List ids = new ArrayList();
    List<CharacterObj> characters;
    if (values != null
      && values.size() > 0) {
      characters = getCharactersFromValues(values);
    } else {
      characters = new ArrayList<>();
    }

    for (CharacterObj ch : characters) {
      List<CharacterObj> singleList = new ArrayList<>();
      singleList.add(ch);
      String id = generateObjects(singleList).get(0);
    }

    return generateObjects(characters);
  }

  List<CharacterObj> getCharactersFromValues(List<String> values) {
    List<CharacterObj> chars = new ArrayList<>();
    for (String value : values) {
      CharacterObj ch = getCharacterFromValue(value);
      chars.add(ch);
    }
    return null;
  }

  CharacterObj getCharacterFromValue(String value) {
    CharacterObj ch = new CharacterObj();
    ch.setValue(value.charAt(0));
    return ch;
  }

  @Override
  protected Map<EntityProperty, List<String>> handleRestrictions(Object forObject, List<EntityRestriction> restrictions)
    throws ProviderException {

    return null;
  }
}
