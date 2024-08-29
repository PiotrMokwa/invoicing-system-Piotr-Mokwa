package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@Slf4j
@Service
public class InvoiceService {

  private final Database dataBase;

  public InvoiceService(Database database) {
    this.dataBase = database;

  }

  Optional<Invoice> getById(int id) {
    log.info("Invoice Service getById");
    return dataBase.getById(id);
  }

  List<Invoice> getAll() {
    log.info("Invoice Service getAll");
    return dataBase.getAll();
  }

  Optional<Invoice> update(int id, Invoice updateInvoice) {
    log.info("Invoice Service update");
    return dataBase.update(id, updateInvoice);
  }

  boolean delete(int id) {
    log.info("Invoice Service delete");
    return dataBase.delete(id);
  }

  public int save(Invoice invoice) {
    log.info("Invoice Service save");
    return dataBase.save(invoice);
  }
}
