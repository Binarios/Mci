package com.aegean.icsd.mciwebapp.object.interfaces;

import java.util.List;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;

public interface IWordProvider {
  Word getWordWithValue(String value) throws ProviderException;

  List<Word> getNewWordsFor(String entityName, int count, Word criteria) throws ProviderException;

  Word getNewWordFor(String entityName, int length) throws ProviderException;

  Word selectWordByNode(String nodeName) throws ProviderException;

  Word selectWordByWordId(String wordId) throws ProviderException;

  List<Word> selectWordsByEntityId(String entityId) throws ProviderException;

  boolean areSynonyms(Word thisWord, Word otherWord) throws ProviderException;

  boolean areAntonyms(Word thisWord, Word otherWord) throws ProviderException;
}
