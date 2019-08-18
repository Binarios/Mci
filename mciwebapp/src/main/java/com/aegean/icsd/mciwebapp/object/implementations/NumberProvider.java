package com.aegean.icsd.mciwebapp.object.implementations;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aegean.icsd.mciwebapp.object.beans.NumberObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.interfaces.INumberProvider;

@Service
public class NumberProvider implements INumberProvider {


  @Override
  public List<String> getNewNumbersInRangeFor(String entityName, int count, NumberObj criteria) {
    return null;
  }

  @Override
  public NumberObj selectNumberByValue(Long value) throws ProviderException {
    return null;
  }

  @Override
  public NumberObj selectNumberByNumberNode(String nodeName) throws ProviderException {
    return null;
  }
}
