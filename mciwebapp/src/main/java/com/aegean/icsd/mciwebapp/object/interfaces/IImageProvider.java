package com.aegean.icsd.mciwebapp.object.interfaces;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IImageProvider {
  String getImageId() throws ProviderException;
}
