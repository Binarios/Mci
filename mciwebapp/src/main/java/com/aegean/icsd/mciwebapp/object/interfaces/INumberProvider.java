package com.aegean.icsd.mciwebapp.object.interfaces;

import com.aegean.icsd.mciwebapp.object.beans.NumberObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface INumberProvider {
  NumberObj selectNumberByValue(Long value) throws ProviderException;
  NumberObj selectNumberByNumberNode(String nodeName) throws ProviderException;
}
