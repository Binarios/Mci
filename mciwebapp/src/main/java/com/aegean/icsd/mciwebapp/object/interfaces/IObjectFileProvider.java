package com.aegean.icsd.mciwebapp.object.interfaces;

import java.util.List;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IObjectFileProvider {
  String getFileLineFromUrl(String url) throws ProviderException;
  List<String> getLines(String url) throws ProviderException;
}
