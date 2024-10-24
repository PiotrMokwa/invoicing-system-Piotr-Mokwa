package pl.futurecollars.invoicing.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@Slf4j
@Service
public class InvoiceService {

  private final Database<Invoice> dataBase;

  public InvoiceService(Database<Invoice> database) {
    this.dataBase = database;

  }

  public Invoice getById(Long id) {
    log.info("Invoice Service getById");
    return dataBase.getById(id);
  }

  public List<Invoice> getAll() {
    log.info("Invoice Service getAll");
    return dataBase.getAll().isEmpty() ? null : dataBase.getAll();
  }

  public Invoice update(Long id, Invoice updateInvoice) {
    log.info("Invoice Service update");
    return dataBase.update(id, updateInvoice);
  }

  public Invoice delete(Long id) {
    log.info("Invoice Service delete");
    return dataBase.delete(id);
  }

  public Long save(Invoice invoice) {
    log.info("Invoice Service save");
    return dataBase.save(invoice);
  }

}

