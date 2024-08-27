package pl.futurecollars.invoicing.db.file;

import java.nio.file.Path;
import lombok.Data;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.InvoiceSetup;
import pl.futurecollars.invoicing.service.FileService;

@Service
@Data
public class IdService {

  private FileService fileService;
  private int nextId;
  private final Path fileId;

  public IdService(InvoiceSetup invoiceSetup) {
    this.fileId = Path.of(invoiceSetup.getLastIdFilePath());
    this.fileService = new FileService();
    this.nextId = getNextId();
  }

  public void setNextId() {

    fileService.writeToFile(fileId, String.valueOf(++this.nextId));
  }

  public int getNextId() {
    String firstInvoiceId = "1";
    boolean isFirstInvoice = fileService.readAllLines(fileId).isEmpty();
    String number = isFirstInvoice ? firstInvoiceId : fileService.readAllLines(fileId).get(0);
    return Integer.parseInt(number);
  }
}
