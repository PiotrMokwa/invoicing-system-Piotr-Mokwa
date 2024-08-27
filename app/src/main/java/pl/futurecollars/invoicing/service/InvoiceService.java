package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@Service
public class InvoiceService {

  private final Database dataBase;

  public InvoiceService(@Qualifier("fileBaseDataBase") Database database) {
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

  public int save(Invoice invoice) {
    return dataBase.save(invoice);
  }
}
