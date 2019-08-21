package com.aegean.icsd.mciobjects.common.interfaces;

import java.util.List;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;

public interface IObjectFileProvider {
  String getFileLineFromUrl(String url) throws ProviderException;
  List<String> getLines(String url) throws ProviderException;
}
