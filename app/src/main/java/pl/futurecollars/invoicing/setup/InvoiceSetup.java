package pl.futurecollars.invoicing.setup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class InvoiceSetup {

  private String fileBase;
  private String lastIdFilePath;

  public InvoiceSetup(String fileBasePath, String lastIdFilePath) {

    this.fileBase = fileBasePath;
    this.lastIdFilePath = lastIdFilePath;
    if (!Files.exists(Path.of(this.fileBase))) {
      createBaseFile(Path.of(this.fileBase));
      log.info("File base was created");
    }
    if (!Files.exists(Path.of(this.lastIdFilePath))) {
      createLastIdFile(Path.of(this.lastIdFilePath));
      writeFirstInvoiceId(Path.of(this.lastIdFilePath));
      log.info("File base was created");
      log.info("Last Id File was created");
    }
  }

  private static void createBaseFile(Path fileBasePath) {

    try {
      Files.createFile(fileBasePath);
    } catch (IOException exception) {
      System.out.println(exception.getMessage());
    }
  }

  private static void createLastIdFile(Path lastIdFile) {

    try {
      Files.createFile(lastIdFile);
    } catch (IOException exception) {
      System.out.println(exception.getMessage());
    }
  }

  private static void writeFirstInvoiceId(Path lastIdFile) {

    try {
      Files.writeString(lastIdFile, "1");
    } catch (IOException exception) {
      System.out.println(exception.getMessage());
    }
  }
}
