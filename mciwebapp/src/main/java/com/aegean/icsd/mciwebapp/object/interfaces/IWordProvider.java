package com.aegean.icsd.mciwebapp.object.interfaces;

import java.util.List;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IWordProvider {
  List<String> getWordsIds(int number) throws ProviderException;
  String getWordFromValue(String value) throws ProviderException;
}
