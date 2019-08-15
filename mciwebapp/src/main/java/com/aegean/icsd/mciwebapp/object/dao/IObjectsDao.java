package com.aegean.icsd.mciwebapp.object.dao;

import java.util.List;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;

public interface IObjectsDao {

  List<String> getNewWordIdsFor(String forEntity) throws ProviderException;

  List<String> getAssociatedWordOfId(String id) throws ProviderException;

  boolean areSynonyms(Word thisWord, Word otherWord) throws ProviderException;

  boolean areAntonyms(Word thisWord, Word otherWord) throws ProviderException;
}
