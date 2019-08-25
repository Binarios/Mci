package com.aegean.icsd.mciobjects.images.implementations;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.common.implementations.ProviderExceptions;
import com.aegean.icsd.mciobjects.images.beans.Image;

class ImageUtils {
  private static final String ROOT_PATH = "../webapps/mci/images";

  private static Logger LOGGER = Logger.getLogger(ImageUtils.class);

  Image[][] splitImage(Image image, int rows, int cols) throws ProviderException {
    LOGGER.info(String.format("Begin splitting image %s in %s rows and %s columns", image.getId(), rows, cols));

    String storedPath = downloadImage(image);

    BufferedImage bufferedImage = readImageFromPath(storedPath);
    BufferedImage[][] bufferedImages = splitBufferedImage(bufferedImage, rows, cols);

    return writeBufferedImages(image, bufferedImages, rows, cols);
  }

  String downloadImage(Image image) throws ProviderException {

    String imageName = getImageName(image);
    try (InputStream in = new URL(image.getPath()).openStream()) {

      String localPath = ROOT_PATH + "/" + imageName;
      LOGGER.info(String.format("Downloading image %s at %s ", image.getId(), localPath));
      File dir = new File(localPath);
      if (!dir.exists()) {
        dir.mkdirs();
      }
      String localName = localPath + "/" + getImageNameWithExtension(image);
      Files.copy(in, Paths.get(localName), StandardCopyOption.REPLACE_EXISTING);
      return localName;
    } catch (IOException e) {
      throw ProviderExceptions.UnableToGetObject(Image.NAME,e);
    }
  }

  BufferedImage readImageFromPath (String path) throws ProviderException {
    File file = new File(path);
    FileInputStream fis;
    BufferedImage bufferedImage;
    try {
      fis = new FileInputStream(file);
      bufferedImage = ImageIO.read(fis);
    } catch (IOException e) {
      throw ProviderExceptions.UnableToGetObject(Image.NAME, e);
    }
    return bufferedImage;
  }

  BufferedImage[][] splitBufferedImage(BufferedImage image, int rows, int cols) {
    int chunkWidth = image.getWidth() / cols;
    int chunkHeight = image.getHeight() / rows;
    BufferedImage[][] bufferedImages = new BufferedImage[rows][cols];

    LOGGER.info("Splitting image ...");
    for (int x = 0; x < rows; x++) {
      for (int y = 0; y < cols; y++) {
        bufferedImages[x][y] = new BufferedImage(chunkWidth, chunkHeight, image.getType());
        Graphics2D gr = bufferedImages[x][y].createGraphics();
        gr.drawImage(image, 0, 0, chunkWidth, chunkHeight,
          chunkWidth * y, chunkHeight * x,
          chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight,
          null);
        gr.dispose();
      }
    }
    LOGGER.info("Splitting done");
    return bufferedImages;
  }

  Image[][] writeBufferedImages(Image image, BufferedImage[][] images, int rows, int cols)
    throws ProviderException {
    Image[][] splits = new Image[rows][cols];

    String imageName = getImageName(image);
    String extension = getImageExtension(image);
    String chunkFolderPath = ROOT_PATH + "/" + imageName;

    for(int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        String chunkName = imageName + "_" + row + "_" + col + "." + extension;
        String chunkPath = chunkFolderPath +  "/" + chunkName;

        writeBufferedImage(chunkPath, extension, images[row][col]);

        Image chunkImage = new Image();
        chunkImage.setPath(chunkPath);
        splits[row][col] = chunkImage;
      }
    }
    return splits;
  }

  void writeBufferedImage(String path, String extension, BufferedImage image) throws ProviderException {
    try {
      LOGGER.info(String.format("Writing image at %s", path));
      ImageIO.write(image, extension, new File(path));
    } catch (IOException e) {
      throw ProviderExceptions.UnableToGetObject(Image.NAME, e);
    }
  }

  String getImageName(Image image) {
    String withExtension = getImageNameWithExtension(image);
    String[] nameFragments = withExtension.split("\\.");
    return nameFragments[0];
  }

  String getImageExtension(Image image) {
    String withExtension = getImageNameWithExtension(image);
    String[] nameFragments = withExtension.split("\\.");
    return nameFragments[1];
  }

  String getImageNameWithExtension(Image image) {
    String path = image.getPath();
    String[] fragments = path.split("/");
    return fragments[fragments.length - 1];
  }
}
