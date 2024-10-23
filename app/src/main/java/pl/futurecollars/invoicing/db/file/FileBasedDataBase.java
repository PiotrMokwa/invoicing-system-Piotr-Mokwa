package pl.futurecollars.invoicing.db.file;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.WithId;
import pl.futurecollars.invoicing.service.FileService;
import pl.futurecollars.invoicing.service.JsonService;
import pl.futurecollars.invoicing.setup.InvoiceSetup;

@AllArgsConstructor
@Slf4j
@Data
public class FileBasedDataBase<T extends WithId> implements Database<T> {

  public JsonService jsonService;
  private final Path fileBase;
  private final Path fileId;
  private FileService fileService;
  private IdService invoiceId;
  private final Class<T> classType;

  public FileBasedDataBase(InvoiceSetup invoiceSetup, Class<T> classType) {

    this.fileBase = Path.of(invoiceSetup.getFileBase());
    this.fileId = Path.of(invoiceSetup.getLastIdFilePath());
    this.classType = classType;
    this.jsonService = new JsonService();
    this.fileService = new FileService();
    this.invoiceId = new IdService(invoiceSetup);

  }

  @Override
  public Long save(T item) {
    Long invoiceId = this.invoiceId.getId();
    item.setId(invoiceId);
    this.invoiceId.setNextId();
    fileService.appendLineToFile(fileBase, jsonService.convertToJson(item));
    log.info("Invoice save");
    return invoiceId;
  }

  @Override
  public T getById(Long id) {
    T invoice = null;
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
  public List<T> getAll() {

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
    log.info(" Get all item !!!");
    String startCollectionsSign = "[";
    String endCollectionsSign = "]";
    return jsonService.convertToObjectList(startCollectionsSign + readedInvoicesWithoutLastSeparator + endCollectionsSign, classType);
  }

  @Override
  public T delete(Long id) {
    int standartizeIdBetwenBases = 1;
    List<T> listOfitems = getAll();
    T delatedInvoice = null;
    try {
      delatedInvoice = listOfitems.remove(id.intValue() - standartizeIdBetwenBases);
    } catch (IndexOutOfBoundsException e) {
      log.warn(e.toString());
    }
    fileService.writeLinesToFile(fileBase, listOfitems.stream().map(value -> jsonService.convertToJson(value)).collect(Collectors.toList()));
    log.info("Delete  !!! Invoice");
    return delatedInvoice;
  }

  @Override
  public T update(Long id, T updatedItem) {
    List<T> listOfInvoice = this.getAll();
    updatedItem.setId(id);
    int standartizeIdBetwenBases = 1;
    T deletedInvoice = null;
    try {
      deletedInvoice = listOfInvoice.set(id.intValue() - standartizeIdBetwenBases, updatedItem);
    } catch (IndexOutOfBoundsException e) {
      log.warn(e.toString());
    }
    fileService.writeLinesToFile(fileBase, listOfInvoice.stream().map(value -> jsonService.convertToJson(value)).collect(Collectors.toList()));
    log.info("Update !!! Invoice");
    return deletedInvoice;
  }

  //  public BigDecimal visit(Predicate<Invoice> rules, Function<InvoiceEntry, BigDecimal> entry) {
  //
  //    return getAll()
  //        .stream()
  //        .filter(rules)
  //        .map(value -> value.getListOfInvoiceEntry()
  //            .stream()
  //            .map(entry)
  //            .reduce(BigDecimal.ZERO, BigDecimal::add)
  //        )
  //         .reduce(BigDecimal.ZERO, BigDecimal::add);
  //  }
}
