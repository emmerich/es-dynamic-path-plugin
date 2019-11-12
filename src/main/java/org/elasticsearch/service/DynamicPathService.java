package org.elasticsearch.service;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
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

  public void appendLine(String path, String line) throws FileSystemException {
    // Append line to path
    Path absPath = getAbsolutePath(path);
    String lineWithNewLine = line.concat("\r\n");

    LOGGER.debug("Adding line {} to path {}", line, absPath);

    doPrivileged(() -> {
      if (!Files.exists(absPath)) {
        Files.createDirectories(absPath.getParent());
        Files.write(absPath, lineWithNewLine.getBytes(), StandardOpenOption.CREATE);
      } else {
        Files.write(absPath, lineWithNewLine.getBytes(), StandardOpenOption.APPEND);
      }

      return null;
    });
  }

  public void removeLine(String path, String line) throws FileSystemException {
    Path absPath = getAbsolutePath(path);

    LOGGER.debug("Removing line {} from path {}", line, absPath);

    doPrivileged(() -> {
      if (Files.exists(absPath)) {
        List<String> filtered = Files.lines(absPath).filter(l -> !linesAreEqual(l, line)).collect(Collectors.toList());
        Files.write(absPath, filtered);
      } else {
        LOGGER.warn("Tried to remove line from {}, but file does not exist", absPath);
      }

      return null;
    });
  }

  public void createFile(String path, String line) throws FileSystemException {
    Path absPath = getAbsolutePath(path);
    String lineWithNewLine = line.concat("\r\n");

    doPrivileged(() -> {
      if (!Files.exists(absPath)) {
        LOGGER.debug("Creating file {}", absPath);
        Files.createDirectories(absPath.getParent());
        Files.write(absPath, lineWithNewLine.getBytes(), StandardOpenOption.CREATE);
      } else {
        LOGGER.debug("Not creating file {}, it already exists", absPath);
      }

      return null;
    });
  }

  public List<String> getLines(String path) throws FileSystemException {
    Path absPath = getAbsolutePath(path);

    LOGGER.debug("Getting lines from path {}", absPath);

    try {
      return AccessController.doPrivileged((PrivilegedExceptionAction<List<String>>) () -> {
        if (Files.exists(absPath)) {
          return Files.lines(absPath)
              .filter(l -> !lineIsComment(l))
              .collect(Collectors.toList());
        } else {
          LOGGER.warn("Cannot get line for path {}: file does not exist", absPath);
          throw new FileSystemNotFoundException();
        }
      });
    } catch (PrivilegedActionException e) {
      if (e.getException() instanceof FileSystemException) {
        throw (FileSystemException) e.getException();
      } else {
        e.printStackTrace();
        return new ArrayList<>();
      }
    }
  }

  private boolean lineIsComment(String line) {
    return line.startsWith("#");
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

  private void doPrivileged(PrivilegedExceptionAction<Void> action) throws FileSystemException {
    try {
      AccessController.doPrivileged(action);
    } catch (PrivilegedActionException e) {
      if (e.getException() instanceof FileSystemException) {
        throw (FileSystemException) e.getException();
      }
    }
  }

}
