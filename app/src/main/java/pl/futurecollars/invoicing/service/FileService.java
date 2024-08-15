package pl.futurecollars.invoicing.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public class FileService {

  private final Path file;

  public FileService(Path file) {

    this.file = file;
    try {
      Files.createFile(file);
    } catch (IOException exception) {
      exception.getStackTrace();
    }
  }

  public void appendLineToFile(String invoiceToWrite) {
    List<String> lines = new ArrayList<>();
    lines.add(invoiceToWrite);
    try {
      Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    } catch (IOException exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    }
  }

  public void writeToFile(String text) {

    try {
      Files.write(file, text.getBytes(), StandardOpenOption.WRITE);

    } catch (IOException exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    }
  }

  public void writeLinesToFile(List<String> text) {

    try {
      Files.write(file, text);

    } catch (IOException exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    }
  }

  public List<String> readAllLines() {

    List<String> fileDataArray = new ArrayList<>();
    try {
      fileDataArray = Files.readAllLines(file, StandardCharsets.UTF_8);

    } catch (IOException exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    }
    return fileDataArray;
  }
}
