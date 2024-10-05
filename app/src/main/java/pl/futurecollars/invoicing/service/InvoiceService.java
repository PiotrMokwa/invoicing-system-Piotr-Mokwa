package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@Slf4j
@Service
public class InvoiceService {

  private final Database dataBase;

  public InvoiceService(Database database) {
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

  public BigDecimal visit(Predicate<Invoice> rules, Function<InvoiceEntry, BigDecimal> entry) {
    log.info("Invoice Service visit");
    return dataBase.visit(rules, entry);
  }
}

