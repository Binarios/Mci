package com.aegean.icsd.mciwebapp.object.interfaces;

import java.util.List;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IObjectProvider {
  List<String> getObjectsIds(String objectName, int number) throws ProviderException;
}
