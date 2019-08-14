package com.aegean.icsd.mciwebapp.object.interfaces;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;

public interface IWordProvider {
  Word getWordWithValue(String value) throws ProviderException;

  Word getNewWordFor(String entityName, int length) throws ProviderException;

  Word getWordFromNode(String wordNode) throws ProviderException;
}
