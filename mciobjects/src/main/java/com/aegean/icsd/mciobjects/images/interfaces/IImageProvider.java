package com.aegean.icsd.mciobjects.images.interfaces;

import java.util.List;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.images.beans.Image;
import com.aegean.icsd.mciobjects.images.beans.ImageData;
import com.aegean.icsd.mciobjects.words.beans.Word;

public interface IImageProvider {
  Image selectImageByNode(String nodeName) throws ProviderException;

  List<Image> selectImagesByEntityId(String entityId) throws ProviderException;

  String selectAssociatedSubject(String imageId) throws ProviderException;

  List<String> getImageIds() throws ProviderException;

  List<Image> getNewOrderedImagesFor(String entityName, int cardinality) throws ProviderException;

  List<Image> selectNewImagesForEntity(String entityName, int count) throws ProviderException;

  Image selectRandomImageWithSubject(Word word) throws ProviderException;

  boolean isSolutionCorrect(List<ImageData> solution) throws ProviderException;
}
