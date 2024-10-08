package pl.futurecollars.invoicing.db.memory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@AllArgsConstructor
public class InMemoryDatabase implements Database {

  private Long nextId;
  private final Map<Integer, Invoice> invoices;

  public InMemoryDatabase(Long nextId) {
    this.nextId = nextId;
    this.invoices = new HashMap<>();
  }

  @Override
  public Long save(Invoice invoice) {
    invoice.setId(nextId);
    invoices.put(nextId.intValue(), invoice);
    Long addedInvoiceId = nextId;
    nextId++;
    return addedInvoiceId;
  }

  @Override
  public Invoice getById(Long id) {
    return invoices.get(id.intValue());
  }

  @Override
  public List<Invoice> getAll() {
    return new ArrayList<>(invoices
        .values());
  }

  @Override
  public Invoice update(Long id, Invoice updateInvoice) {
    boolean isInvoiceNotInBase = !invoices.containsKey(id.intValue());
    return isInvoiceNotInBase ? null : invoices.put(id.intValue(), updateInvoice);
  }

  @Override
  public Invoice delete(Long id) {
    return invoices.remove(id.intValue());
  }

  public BigDecimal visit(Predicate<Invoice> rules, Function<InvoiceEntry, BigDecimal> entry) {

    return getAll()
        .stream()
        .filter(rules)
        .map(value -> value.getListOfInvoiceEntry()
            .stream()
            .map(entry)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
        )
        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.UP);
  }
}
