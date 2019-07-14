package com.aegean.icsd.engine.providers.interfaces;

public interface IValueProvider {
  int getPositiveValue (int min, int max);
  String getStringValue (String association);
  String getAssetUri (String association);
}
