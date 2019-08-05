package com.aegean.icsd.mciwebapp.providers.interfaces;

import java.util.List;

import com.aegean.icsd.mciwebapp.providers.beans.ProviderException;
import com.aegean.icsd.mciwebapp.providers.objects.Word;

public interface IWordProvider {
  List<Word> getWords(int number) throws ProviderException;

}
