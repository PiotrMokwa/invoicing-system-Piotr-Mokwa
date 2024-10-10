package pl.futurecollars.invoicing.db;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

public interface Database {

  Long save(Invoice invoice);

  Invoice getById(Long id);

  List<Invoice> getAll();

  Invoice update(Long id, Invoice updateInvoice);

  Invoice delete(Long id);

  BigDecimal visit(Predicate<Invoice> rules, Function<InvoiceEntry, BigDecimal> entry);

}
