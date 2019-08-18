package com.aegean.icsd.mciwebapp.object.interfaces;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ImageData;
import com.aegean.icsd.mciwebapp.object.beans.Image;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IImageProvider {
  Image selectImageByNode(String nodeName) throws ProviderException;

  List<Image> selectImagesByEntityId(String entityId) throws ProviderException;

  String selectAssociatedSubject(String imageId) throws ProviderException;

  List<String> getImageIds() throws ProviderException;

  List<Image> getNewOrderedImagesFor(String entityName, int cardinality) throws ProviderException;

  boolean isSolutionCorrect(List<ImageData> solution) throws ProviderException;
}
