package com.aegean.icsd.mciwebapp.object.interfaces;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.WordCriteria;

public interface IWordProvider {
  String getWordFromValue(String value) throws ProviderException;

  String getWordWithCriteria(WordCriteria criteria) throws ProviderException;

  String getWordValue(String wordId) throws ProviderException;
}
