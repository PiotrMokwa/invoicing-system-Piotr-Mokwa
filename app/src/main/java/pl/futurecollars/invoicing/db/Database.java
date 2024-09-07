package pl.futurecollars.invoicing.db;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

public interface Database {

  int save(Invoice invoice);

  Invoice getById(int id);

  List<Invoice> getAll();

  Invoice update(int id, Invoice updateInvoice);

  Invoice delete(int id);

  BigDecimal visit(Predicate<Invoice> rules, Function<InvoiceEntry, BigDecimal> entry);

}
