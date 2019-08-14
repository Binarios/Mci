package com.aegean.icsd.mciwebapp.object.interfaces;

import com.aegean.icsd.mciwebapp.object.beans.Image;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IImageProvider {
  Image getImage() throws ProviderException;
}
