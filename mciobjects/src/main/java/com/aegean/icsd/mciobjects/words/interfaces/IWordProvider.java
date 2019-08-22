package com.aegean.icsd.mciobjects.words.interfaces;

import java.util.List;

import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.mciobjects.words.beans.Word;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;

public interface IWordProvider {
  Word getWordWithValue(String value) throws ProviderException;

  List<Word> getNewWordsFor(String entityName, int count, Word criteria) throws ProviderException;

  Word getNewWordFor(String entityName, int length) throws ProviderException;

  Word selectWordByNode(String nodeName) throws ProviderException;

  Word selectWordById(String wordId) throws ProviderException;

  List<Word> selectWordsByEntityId(String entityId) throws ProviderException;

  List<Word> selectWordsByEntityIdOnProperty(String entityId, EntityProperty onProperty) throws ProviderException;

  boolean areSynonyms(Word thisWord, Word otherWord) throws ProviderException;

  boolean areAntonyms(Word thisWord, Word otherWord) throws ProviderException;
}
