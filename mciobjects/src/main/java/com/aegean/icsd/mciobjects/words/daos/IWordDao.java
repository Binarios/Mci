package com.aegean.icsd.mciobjects.words.daos;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.words.beans.Word;

public interface IWordDao {

  boolean areSynonyms(Word thisWord, Word otherWord) throws ProviderException;

  boolean areAntonyms(Word thisWord, Word otherWord) throws ProviderException;

}
