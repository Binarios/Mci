package com.aegean.icsd.mciwebapp.object.interfaces;

import java.util.List;

import com.aegean.icsd.mciwebapp.object.beans.Image;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IImageProvider {
  Image selectImageByNode(String nodeName) throws ProviderException;

  List<Image> selectImagesByEntityId(String entityId) throws ProviderException;

  Image getImage() throws ProviderException;
}
