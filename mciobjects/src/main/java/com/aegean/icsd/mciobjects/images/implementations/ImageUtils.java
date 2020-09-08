package com.aegean.icsd.mciobjects.images.implementations;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

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
    try (InputStream in = getTrustedUrl(image.getPath()).openStream()) {

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
      throw ProviderExceptions.UnableToGetObject(Image.NAME, e);
    }
  }

  BufferedImage readImageFromPath(String path) throws ProviderException {
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

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        String chunkName = imageName + "_" + row + "_" + col + "." + extension;
        String chunkPath = chunkFolderPath + "/" + chunkName;

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

  URL getTrustedUrl(String url) throws MalformedURLException, ProviderException {
    TrustManager[] trustAllCerts = getTrustManager();

    SSLContext sc = null;
    try {
      sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw ProviderExceptions.UnableToSetupTrustManager(url,e);
    }

    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    // Create all-trusting host name verifier
    HostnameVerifier allHostsValid = getHostnameVerifier();
    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    return new URL(url);
  }

  HostnameVerifier getHostnameVerifier() {
    return new HostnameVerifier() {
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    };
  }

  TrustManager[] getTrustManager() {
    return new TrustManager[]{new X509TrustManager() {
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
      }
      public void checkClientTrusted(X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(X509Certificate[] certs, String authType) {
      }
    }};
  }
}
