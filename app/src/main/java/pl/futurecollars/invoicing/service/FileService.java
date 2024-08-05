package pl.futurecollars.invoicing.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import pl.futurecollars.invoicing.InvoiceSetup;

public class FileService {

  Path fileData;
  Path fileLastInvoiceId;

  public FileService(InvoiceSetup invoiceSetup) {

    this.fileData = invoiceSetup.getFileBase();
    this.fileLastInvoiceId = invoiceSetup.getLastInvoiceId();
    try {
      Files.createFile(this.fileData);
    } catch (IOException exception) {
      exception.getStackTrace();
    }

  }

  public void writeDataToFile(String invoiceToWrite) {

    try {
      Files.writeString(this.fileData, invoiceToWrite, StandardOpenOption.APPEND);

    } catch (IOException exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    }
  }

  public String readDataFromFile() {
    String fileData = "";
    String closeSymbolCollectionJason = " ]";
    try {
      fileData = Files.readString(this.fileData) + closeSymbolCollectionJason;
    } catch (IOException exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    }
    return fileData;
  }

  public void writeLastInvoiceId(Integer lastInvoiceId) {

    try {
      Files.writeString(this.fileLastInvoiceId, String.valueOf(lastInvoiceId));

    } catch (IOException exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    }
  }

  public int readLastInvoiceId() {
    String lastInvoiceId = "";
    try {
      lastInvoiceId = Files.readString(this.fileLastInvoiceId);
    } catch (IOException exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    }

    return Integer.parseInt(lastInvoiceId);
  }

}
