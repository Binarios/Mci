package com.aegean.icsd.mciwebapp.object.implementations;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.mciwebapp.object.beans.CharacterObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IProvider;

@Service
public class ObjectProvider implements IObjectProvider {

  @Autowired
  private IAnnotationReader ano;

  private Map<String, IProvider> providers = new HashMap<>();

  @Override
  public List<String> getObjectsIds(String objectName, int number) throws ProviderException {
    return generateObjects(objectName, null, number);
  }

  @Override
  public List<String> getObjectsIds(String objectName, List<String> values) throws ProviderException {
    return generateObjects(objectName, values, 0);
  }

  List<String> generateObjects(String objectName, List<String> values, int number) throws ProviderException {
    IProvider provider = providers.get(objectName);
    if (provider == null) {
      throw Exceptions.UnableToFindObjectProvider(objectName);
    }
    List<String> result;

    if (number < 0) {
      number = 0;
    }
    if (values != null
      && values.size() > 0) {
      result = provider.getObjectsIds(values);
    } else {
      result = provider.getObjectsIds(number);
    }
    return result;
  }

  @PostConstruct
  void registerObjects() {
    providers.put(Word.NAME, new WordProvider());
    providers.put(CharacterObj.NAME, new CharacterProvider());
  }

}
