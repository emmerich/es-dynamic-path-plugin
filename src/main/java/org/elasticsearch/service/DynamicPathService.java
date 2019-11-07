package org.elasticsearch.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.env.Environment;

public class DynamicPathService {

  private Path configDirectory;
  private final static Logger LOGGER = LogManager.getLogger(DynamicPathService.class);

  public DynamicPathService(Environment environment) {
    configDirectory = environment.configFile().toAbsolutePath();
  }

  public void appendLine(String path, String line) throws IOException {
    // Append line to path
    Path absPath = getAbsolutePath(path);
    String lineWithNewLine = line.concat("\r\n");

    LOGGER.debug("Adding line {} to path {}", line, path);

    doPrivileged(() -> {
      if (!Files.exists(absPath)) {
        Files.write(absPath, lineWithNewLine.getBytes(), StandardOpenOption.CREATE);
      } else {
        Files.write(absPath, lineWithNewLine.getBytes(), StandardOpenOption.APPEND);
      }

      return null;
    });
  }

  public void removeLine(String path, String line) throws IOException {
    Path absPath = getAbsolutePath(path);

    LOGGER.debug("Removing line {} from path {}", line, path);

    doPrivileged(() -> {
      List<String> filtered = Files.lines(absPath).filter(l -> !linesAreEqual(l, line)).collect(Collectors.toList());
      Files.write(absPath, filtered);
      return null;
    });
  }

  private boolean linesAreEqual(String lhs, String rhs) {
    return clean(lhs).equalsIgnoreCase(clean(rhs));
  }

  private String clean(String str) {
    return str
        .replace("\n", "")
        .replace("\r", "");
  }

  private Path getAbsolutePath(String path) {
    return Paths.get(configDirectory.toString(), path);
  }

  private void doPrivileged(PrivilegedExceptionAction<Void> action) throws IOException {
    try {
      AccessController.doPrivileged(action);
    } catch (PrivilegedActionException e) {
      if (e.getException() instanceof IOException) {
        throw (IOException) e.getException();
      }

      e.printStackTrace();
    }
  }

}
