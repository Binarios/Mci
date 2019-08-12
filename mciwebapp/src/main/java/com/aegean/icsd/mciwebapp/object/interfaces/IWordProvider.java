package com.aegean.icsd.mciwebapp.object.interfaces;

import java.util.List;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IWordProvider {
  String getWordFromValue(String value) throws ProviderException;
  List<String> getWordIdsWithLength(int length) throws ProviderException;
  String createWord(int length);
}
