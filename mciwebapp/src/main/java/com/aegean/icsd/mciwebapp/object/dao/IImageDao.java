package com.aegean.icsd.mciwebapp.object.dao;

import java.util.List;
import java.util.Map;

import com.aegean.icsd.mciwebapp.common.beans.ImageData;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IImageDao {
  Map<String, List<String>> getOrderedImages(int nb) throws ProviderException;
  boolean rootOrderImageExistsFor(String rootImageId, String entityName) throws ProviderException;
  boolean isOrderCorrect(List<ImageData> solution) throws ProviderException;
}
