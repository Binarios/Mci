package com.aegean.icsd.mciwebapp.object.interfaces;

import java.util.List;

import com.aegean.icsd.mciwebapp.object.beans.NumberObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface INumberProvider {
  List<String> getNewNumbersInRangeFor(String entityName, int count, NumberObj criteria);
  NumberObj selectNumberByValue(Long value) throws ProviderException;
  NumberObj selectNumberByNumberNode(String nodeName) throws ProviderException;
}
