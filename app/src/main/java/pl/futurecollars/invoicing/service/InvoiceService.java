package pl.futurecollars.invoicing.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

public class InvoiceService {

  Map<Integer, Invoice> invoices = new HashMap<>();
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

  void update(int id, Invoice updateInvoice) {
    dataBase.update(id, updateInvoice);
  }

  boolean delete(int id) {
    Optional<Invoice> deletedInvoice = Optional
        .ofNullable(invoices.remove(id));
    return deletedInvoice.isPresent();
  }

}
