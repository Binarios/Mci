package com.aegean.icsd.mciwebapp.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class Utils {

  public String getFileLine(String filePath) throws IOException {
    Supplier<Stream<String>> streamSupplier = () -> {
      try {
        return Files.lines(Paths.get(filePath));
      } catch (IOException e) {
        return null;
      }
    };

    if (streamSupplier.get() == null ) {
      throw new IOException(String.format("Error when reading file at: %s", filePath));
    }

    long totalNb = streamSupplier.get().count();
    long lineNumber = ThreadLocalRandom.current().nextLong(0, totalNb);
    String line = streamSupplier.get().skip(lineNumber).findFirst().orElse(null);
    if (line == null) {
      throw new IOException("Could not read the line");
    }
    return line.replace("\\r", "").trim();
  }
}
