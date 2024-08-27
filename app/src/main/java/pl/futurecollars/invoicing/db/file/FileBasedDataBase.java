package pl.futurecollars.invoicing.db.file;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.stereotype.Repository;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.FileService;
import pl.futurecollars.invoicing.service.JsonService;
import pl.futurecollars.invoicing.setup.InvoiceSetup;

@Repository
@Data
public class FileBasedDataBase implements Database {

  private IdService invoiceId;
  private JsonService jsonService;
  private FileService fileService;
  private final Path fileBase;
  private final Path fileId;

  public FileBasedDataBase(InvoiceSetup invoiceSetup) {
    this.fileBase = Path.of(invoiceSetup.getFileBase());
    this.fileId = Path.of(invoiceSetup.getLastIdFilePath());
    this.jsonService = new JsonService(invoiceSetup);
    this.fileService = new FileService();
    this.invoiceId = new IdService(invoiceSetup);

  }

  @Override
  public int save(Invoice invoice) {
    int invoiceId = this.invoiceId.getId();
    invoice.setId(invoiceId);

    this.invoiceId.setNextId();
    fileService.appendLineToFile(fileBase, jsonService.convertToJson(invoice));
    return invoiceId;
  }

  @Override
  public Optional<Invoice> getById(int id) {
    Invoice invoice = null;
    int standartizeIdBetwenBases = 1;
    try {
      invoice = getAll().get(id - standartizeIdBetwenBases);
    } catch (IndexOutOfBoundsException exception) {
      exception.printStackTrace();
    }
    return Optional.ofNullable(invoice);
  }

  @Override
  public List<Invoice> getAll() {

    String startCollectionsSign = "[";
    String endCollectionsSign = "]";
    List<String> allLines = fileService.readAllLines(fileBase);
    String readedInvoices;
    readedInvoices = String.join("", allLines);
    String readedInvoicesWithoutLastSeparator = "";
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
    int standartizeIdBetwenBases = 1;
    List<Invoice> listOfInvoice = getAll();
    int startSizeOfList = listOfInvoice.size();
    System.out.println(startSizeOfList);
    try {
      listOfInvoice.remove(id - standartizeIdBetwenBases);
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
    int sizeOfListAfterDeleting = listOfInvoice.size();
    fileService.writeLinesToFile(fileBase,
        listOfInvoice
            .stream()
            .map(value -> jsonService.convertToJson(value))
            .collect(Collectors.toList())
    );
    return startSizeOfList == sizeOfListAfterDeleting + 1;
  }

  @Override
  public Optional<Invoice> update(int id, Invoice updateInvoice) {
    List<Invoice> listOfInvoice = this.getAll();
    updateInvoice.setId(id);
    int standartizeIdBetwenBases = 1;
    Invoice deletedInvoice = null;
    try {
      deletedInvoice = listOfInvoice.set(id - standartizeIdBetwenBases, updateInvoice);
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
    fileService.writeLinesToFile(fileBase,
        listOfInvoice
            .stream()
            .map(value -> jsonService.convertToJson(value))
            .collect(Collectors.toList())
    );
    return Optional.ofNullable(deletedInvoice);
  }
}
