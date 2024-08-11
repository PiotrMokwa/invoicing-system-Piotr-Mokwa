package pl.futurecollars.invoicing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Data;

@Data
public class InvoiceSetup {

  private Path fileBase;
  private Path lastIdFilePath;

  public InvoiceSetup(String fileBasePath, String lastIdFilePath) {

    this.fileBase = Path.of(fileBasePath);
    this.lastIdFilePath = Path.of(lastIdFilePath);
    if (!Files.exists(this.fileBase)) {
      createBaseFile(this.fileBase);
    }
    if (!Files.exists(this.lastIdFilePath)) {
      createLastIdFile(this.lastIdFilePath);
      writeFirstInvoiceId(this.lastIdFilePath);
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
      Files.writeString(lastIdFile, "0");
    } catch (IOException exception) {
      System.out.println(exception.getMessage());
    }
  }
}
