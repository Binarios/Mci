package com.aegean.icsd.mciobjects.images.daos;

import java.util.List;
import java.util.Map;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.images.beans.ImageData;

public interface IImageDao {
  Map<String, List<String>> getOrderedImages(int nb) throws ProviderException;
  boolean rootOrderImageExistsFor(String rootImageId, String entityName) throws ProviderException;
  boolean isOrderCorrect(List<ImageData> solution) throws ProviderException;
}
