package pl.futurecollars.invoicing.db.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private Map<Integer, Invoice> invoices;

  public FileBasedDataBase(InvoiceSetup invoiceSetup) {

    this.invoices = new HashMap<>();
    this.jsonService = new JsonService();
    this.fileService = new FileService(invoiceSetup);
    int firstId = this.fileService.readLastInvoiceId();
    this.invoiceId = new IdService(firstId);
  }

  @Override
  public boolean save(Invoice invoice) {
    int nextId = this.invoiceId.getNextId();
    nextId++;
    Invoice adedInvoice;
    invoice.setId(nextId);
    adedInvoice = invoices.put(nextId, invoice);
    this.invoiceId.setNextId(nextId);

    return Optional
        .ofNullable(adedInvoice)
        .isEmpty();
  }

  @Override
  public Optional<Invoice> getById(int id) {
    return Optional.ofNullable(invoices.get(id));
  }

  @Override
  public List<Invoice> getAll() {
    return new ArrayList<>(invoices
        .values());
  }

  @Override
  public void update(int id, Invoice updateInvoice) {
    invoices.put(id, updateInvoice);
  }

  @Override
  public boolean delete(int id) {
    Optional<Invoice> deletedInvoice = Optional
        .ofNullable(invoices.remove(id));
    return deletedInvoice.isPresent();
  }

  public List<Invoice> getNewInvoices() {
    List<Invoice> newInvoices = new ArrayList<>();
    this.invoices.forEach((number, invoice) -> {
          boolean isNewInvoice = invoice.getId() > this.invoiceId.getStartId();
          if (isNewInvoice) {
            newInvoices.add(invoice);
          }
        }
    );
    return newInvoices;
  }

  public void writeToBase() {

    this.getNewInvoices()
        .forEach(invoice -> {
              boolean isFirstInvoice = invoice.getId() == 1;
              String startSymbolCollectionJason = "[ ";
              String newObjectSeparatorJason = ", ";
              if (isFirstInvoice) {
                this.fileService
                    .writeDataToFile(startSymbolCollectionJason + jsonService.convertToJson(invoice));
              } else {
                this.fileService
                    .writeDataToFile(newObjectSeparatorJason + jsonService.convertToJson(invoice));
              }
            }
        );
    int newStartId = this.getInvoiceId().getNextId();
    fileService.writeLastInvoiceId(newStartId);
    this.invoiceId.setStartId(newStartId);
  }

  public List<Invoice> printFromBase() {
    String closeSymbolCollectionJason = " ]";
    List<Invoice> readInvice = jsonService.convertToInvoices(fileService.readDataFromFile() + closeSymbolCollectionJason);
    readInvice.forEach(invoice -> System.out.println(invoice.getId())
    );
    return readInvice;
  }
}
