package pl.futurecollars.invoicing.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Getter
public class FileService {

  public FileService() {

  }

  public void appendLineToFile(Path file, String invoiceToWrite) {
    List<String> lines = new ArrayList<>();
    lines.add(invoiceToWrite);
    try {
      Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    } catch (IOException exception) {
      log.warn(exception.toString());
    }
    log.info("appendLineToFile");
  }

  public void writeToFile(Path file, String text) {

    try {
      Files.write(file, text.getBytes(), StandardOpenOption.WRITE);

    } catch (IOException exception) {
      log.warn(exception.toString());
    }
    log.info("writeToFile");
  }

  public void writeLinesToFile(Path file, List<String> text) {

    try {
      Files.write(file, text);

    } catch (IOException exception) {
      log.warn(exception.toString());
    }
    log.info("writeLinesToFile");
  }

  public List<String> readAllLines(Path file) {

    List<String> fileDataArray = new ArrayList<>();
    try {
      fileDataArray = Files.readAllLines(file, StandardCharsets.UTF_8);

    } catch (IOException exception) {
      log.warn(exception.toString());
    }
    log.info("readAllLines");
    return fileDataArray;
  }
}
