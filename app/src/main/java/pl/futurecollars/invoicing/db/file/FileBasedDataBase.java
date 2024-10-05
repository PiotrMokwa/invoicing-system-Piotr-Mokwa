package pl.futurecollars.invoicing.db.file;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.service.FileService;
import pl.futurecollars.invoicing.service.JsonService;
import pl.futurecollars.invoicing.setup.InvoiceSetup;

@Slf4j
@Data
public class FileBasedDataBase implements Database {

  public JsonService jsonService;
  private final Path fileBase;
  private final Path fileId;
  private FileService fileService;
  private IdService invoiceId;

  public FileBasedDataBase(InvoiceSetup invoiceSetup) {

    this.fileBase = Path.of(invoiceSetup.getFileBase());
    this.fileId = Path.of(invoiceSetup.getLastIdFilePath());
    this.jsonService = new JsonService();
    this.fileService = new FileService();
    this.invoiceId = new IdService(invoiceSetup);
  }

  @Override
  public Long save(Invoice invoice) {
    Long invoiceId = this.invoiceId.getId();
    invoice.setId(invoiceId);
    this.invoiceId.setNextId();
    fileService.appendLineToFile(fileBase, jsonService.convertToJson(invoice));
    log.info("Invoice save");
    return invoiceId;
  }

  @Override
  public Invoice getById(Long id) {
    Invoice invoice = null;
    int standartizeIdBetwenBases = 1;
    try {
      invoice = getAll().get(id.intValue() - standartizeIdBetwenBases);
    } catch (IndexOutOfBoundsException exception) {
      log.warn(exception.toString());
    }
    log.info("Invoice getById !!!");
    return invoice;
  }

  @Override
  public List<Invoice> getAll() {

    List<String> allLines = fileService.readAllLines(fileBase);
    String readedInvoices;
    readedInvoices = String.join("", allLines);
    String readedInvoicesWithoutLastSeparator = "";
    if (!allLines.isEmpty()) {
      try {
        readedInvoicesWithoutLastSeparator = readedInvoices.substring(0, readedInvoices.length() - 2);
      } catch (IndexOutOfBoundsException e) {
        log.warn(e.toString());
      }
    }
    log.info(" Get all invoices !!!");
    String startCollectionsSign = "[";
    String endCollectionsSign = "]";
    return jsonService.convertToInvoices(startCollectionsSign + readedInvoicesWithoutLastSeparator + endCollectionsSign);
  }

  @Override
  public Invoice delete(Long id) {
    int standartizeIdBetwenBases = 1;
    List<Invoice> listOfInvoice = getAll();
    Invoice delatedInvoice = null;
    try {
      delatedInvoice = listOfInvoice.remove(id.intValue() - standartizeIdBetwenBases);
    } catch (IndexOutOfBoundsException e) {
      log.warn(e.toString());
    }
    fileService.writeLinesToFile(fileBase, listOfInvoice.stream().map(value -> jsonService.convertToJson(value)).collect(Collectors.toList()));
    log.info("Delete  !!! Invoice");
    return delatedInvoice;
  }

  @Override
  public Invoice update(Long id, Invoice updateInvoice) {
    List<Invoice> listOfInvoice = this.getAll();
    updateInvoice.setId(id);
    int standartizeIdBetwenBases = 1;
    Invoice deletedInvoice = null;
    try {
      deletedInvoice = listOfInvoice.set(id.intValue() - standartizeIdBetwenBases, updateInvoice);
    } catch (IndexOutOfBoundsException e) {
      log.warn(e.toString());
    }
    fileService.writeLinesToFile(fileBase, listOfInvoice.stream().map(value -> jsonService.convertToJson(value)).collect(Collectors.toList()));
    log.info("Update !!! Invoice");
    return deletedInvoice;
  }

  public BigDecimal visit(Predicate<Invoice> rules, Function<InvoiceEntry, BigDecimal> entry) {

    return getAll()
        .stream()
        .filter(rules)
        .map(value -> value.getListOfInvoiceEntry()
            .stream()
            .map(entry)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
        )
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
