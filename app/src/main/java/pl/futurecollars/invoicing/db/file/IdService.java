package pl.futurecollars.invoicing.db.file;

import java.nio.file.Path;
import lombok.Data;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.service.FileService;
import pl.futurecollars.invoicing.setup.InvoiceSetup;

@Service
@Data
public class IdService {

  private FileService fileService;
  private int nextId;
  private final Path fileId;

  public IdService(InvoiceSetup invoiceSetup) {
    this.fileId = Path.of(invoiceSetup.getLastIdFilePath());
    this.fileService = new FileService();
    this.nextId = getId();
  }

  public void setNextId() {
    System.out.println(nextId);
    fileService.writeToFile(fileId, String.valueOf(++this.nextId));
  }

  public int getId() {
    String number = fileService.readAllLines(fileId).get(0);
    return Integer.parseInt(number);
  }
}
