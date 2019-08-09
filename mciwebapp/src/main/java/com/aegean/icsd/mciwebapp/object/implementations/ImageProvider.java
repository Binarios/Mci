package com.aegean.icsd.mciwebapp.object.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.mciwebapp.object.configurations.ImageConfiguration;
import com.aegean.icsd.mciwebapp.object.interfaces.IImageProvider;

@Service
public class ImageProvider implements IImageProvider {

  @Autowired
  private ImageConfiguration configuration;

  @Override
  public List<String> getImagesIds(int totalImageNumber) {
    //TODO implement logic
    return null;
  }
}
