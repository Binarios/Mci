package com.aegean.icsd.mciwebapp.object.interfaces;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IObjectFileProvider {
  String getFileLineFromUrl(String url) throws ProviderException;
}
