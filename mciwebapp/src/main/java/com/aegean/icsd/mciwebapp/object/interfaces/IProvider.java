package com.aegean.icsd.mciwebapp.object.interfaces;

import java.util.List;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IProvider {
  List<String> getObjectsIds(int number) throws ProviderException;
  List<String> getObjectsIds(List<String> values) throws ProviderException;
}
