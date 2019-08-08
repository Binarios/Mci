package com.aegean.icsd.mciwebapp.object.implementations;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IProvider;

@Service
public class ObjectProvider implements IObjectProvider {

  @Autowired
  private IProvider wordProvider;

  private Map<String, IProvider> providers = new HashMap<>();

  @Override
  public List<String> getObjectsIds(String objectName, int number) throws ProviderException {
    IProvider provider = providers.get(objectName);
    if (provider == null) {
      throw Exceptions.UnableToFindObjectProvider(objectName);
    }
    if (number < 0) {
      number = 0;
    }

    List<String> result = provider.getObjectsIds(number);
    return result;
  }

  @PostConstruct
  void registerObjects() {
    providers.put(Word.NAME, wordProvider);
  }

}
