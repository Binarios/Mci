package com.aegean.icsd.mciwebapp.object.implementations;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectFileProvider;

@Service
public class ObjectFileProvider implements IObjectFileProvider {

  private static Logger LOGGER = Logger.getLogger(ObjectFileProvider.class);

  @Override
  public String getFileLineFromUrl(String url) throws ProviderException {
    LOGGER.info(String.format("Retrieving line from file at %s", url));
    String[] chunks = url.split("/");
    String fileName = chunks[chunks.length - 1];

    File file;
    List<String> allLines;
    try {
      file = new File(fileName);
      FileUtils.copyURLToFile(
        new URL(url),
        file, 180000, 180000);
      allLines = Files.readAllLines(Paths.get(file.getPath()));
    } catch (IOException e) {
      throw Exceptions.UnableToGetFileFromUrl(url, fileName, e);
    }

    Supplier<Stream<String>> streamSupplier = allLines::stream;

    if (streamSupplier.get() == null ) {
      throw Exceptions.UnableToReadFile(String.format("Error when reading file at: %s", file.getAbsolutePath()));
    }

    long totalNb = streamSupplier.get().count();
    long lineNumber = ThreadLocalRandom.current().nextLong(0, totalNb);
    String line = streamSupplier.get().skip(lineNumber).findFirst().orElse(null);
    if (line == null) {
      throw Exceptions.UnableToReadFile("Could not read the line");
    }

    return line.replace("\\r", "").trim();
  }
}
