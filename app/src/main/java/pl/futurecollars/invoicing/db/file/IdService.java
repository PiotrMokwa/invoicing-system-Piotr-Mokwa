package pl.futurecollars.invoicing.db.file;

import lombok.Data;
import pl.futurecollars.invoicing.InvoiceSetup;
import pl.futurecollars.invoicing.service.FileService;

@Data
public class IdService {

  FileService fileService;
  private int nextId;

  public IdService(InvoiceSetup invoiceSetup) {

    this.fileService = new FileService(invoiceSetup.getLastIdFilePath());
    this.nextId = getNextId();
  }

  public void setNextId() {

    fileService.writeToFile(String.valueOf(++this.nextId));
  }

  public int getNextId() {

    return Integer.parseInt(fileService.readAllLines().get(0));
  }
}
