package pl.futurecollars.invoicing.db.file;

import java.util.List;
import java.util.Optional;
import lombok.Data;
import pl.futurecollars.invoicing.InvoiceSetup;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.FileService;
import pl.futurecollars.invoicing.service.JsonService;

@Data
public class FileBasedDataBase implements Database {

  private IdService invoiceId;
  private JsonService jsonService;
  private FileService fileService;

  public FileBasedDataBase(InvoiceSetup invoiceSetup) {

    this.jsonService = new JsonService(invoiceSetup);
    this.fileService = new FileService(invoiceSetup.getFileBase());
    this.invoiceId = new IdService(invoiceSetup);
  }

  @Override
  public boolean save(Invoice invoice) {
    int nextId = this.invoiceId.getNextId();
    invoice.setId(nextId);

    this.invoiceId.setNextId();
    fileService.appendLineToFile(jsonService.convertToJson(invoice));
    return true;
  }

  @Override
  public Optional<Invoice> getById(int id) {

    return Optional.ofNullable(getAll().get(id));
  }

  @Override
  public List<Invoice> getAll() {

    String startCollectionsSign = "[";
    String endCollectionsSign = "]";
    List<String> allLines = fileService.readAllLines();
    String readedInvoices = "";
    readedInvoices = String.join("", allLines);
    String readedInvoicesWithoutLastSeparator = null;
    if (!allLines.isEmpty()) {
      try {
        readedInvoicesWithoutLastSeparator = readedInvoices.substring(0, readedInvoices.length() - 2);
      } catch (IndexOutOfBoundsException e) {
        e.printStackTrace();
      }
    }
    return jsonService.convertToInvoices(
        startCollectionsSign
            + readedInvoicesWithoutLastSeparator
            + endCollectionsSign
    );
  }

  @Override
  public boolean delete(int id) {
    List<Invoice> listOfInvoice = getAll();
    int startSizeOfList = listOfInvoice.size();
    Invoice invoice;
    try {
      invoice = listOfInvoice.remove(id);
    } catch (UnsupportedOperationException e) {
      e.printStackTrace();
    }
    int sizeOfListAfterDeleting = listOfInvoice.size();
    fileService.writeLinesToFile(
        listOfInvoice
            .stream()
            .map(value -> jsonService.convertToJson(value))
            .toList()
    );
    return startSizeOfList == sizeOfListAfterDeleting + 1;
  }

  @Override
  public Optional<Invoice> update(int id, Invoice updateInvoice) {
    List<Invoice> listOfInvoice = this.getAll();
    updateInvoice.setId(id);
    Invoice deletedInvoice = null;
    try {
      deletedInvoice = listOfInvoice.set(id, updateInvoice);
    } catch (UnsupportedOperationException e) {
      e.printStackTrace();
    }
    fileService.writeLinesToFile(
        listOfInvoice
            .stream()
            .map(value -> jsonService.convertToJson(value))
            .toList()
    );
    return Optional.ofNullable(deletedInvoice);
  }
}
