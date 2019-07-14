package com.aegean.icsd.engine.providers.implementations;


import java.util.Random;

import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.providers.interfaces.IValueProvider;

@Service
public class ValueProvider implements IValueProvider {

  @Override
  public int getPositiveValue(int min, int max) {
    Random rand = new Random(System.currentTimeMillis());
    int randNumber = 0;

    if (max <= 0) {
      randNumber = rand.nextInt(Integer.MAX_VALUE);
    }

    if (min < 0) {
      min = 0;
    }

    if (max > 0) {
      randNumber = rand.nextInt((max - min) + 1);
    }

    return randNumber + min;
  }

  @Override
  public String getStringValue(String association) {
    return null;
  }

  @Override
  public String getAssetUri(String association) {
    return null;
  }
}