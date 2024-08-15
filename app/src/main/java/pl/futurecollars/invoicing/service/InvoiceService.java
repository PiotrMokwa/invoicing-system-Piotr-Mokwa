package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

public class InvoiceService {

  private final Database dataBase;

  public InvoiceService(Database database) {
    this.dataBase = database;
  }

  Optional<Invoice> getById(int id) {
    return dataBase.getById(id);
  }

  List<Invoice> getAll() {
    return dataBase.getAll();
  }

  Optional<Invoice> update(int id, Invoice updateInvoice) {
    return dataBase.update(id, updateInvoice);
  }

  boolean delete(int id) {
    return dataBase.delete(id);
  }

  public boolean save(Invoice invoice) {
    return dataBase.save(invoice);
  }
}
