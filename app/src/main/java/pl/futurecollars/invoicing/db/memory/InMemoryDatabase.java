package pl.futurecollars.invoicing.db.memory;

import java.math.BigDecimal;
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

  private int nextId;
  private final Map<Integer, Invoice> invoices;

  public InMemoryDatabase(int nextId) {
    this.nextId = nextId;
    this.invoices = new HashMap<>();
  }

  @Override
  public int save(Invoice invoice) {
    invoice.setId(nextId);
    invoices.put(nextId, invoice);
    int addedInvoiceId = nextId;
    nextId++;
    return addedInvoiceId;
  }

  @Override
  public Invoice getById(int id) {
    return invoices.get(id);
  }

  @Override
  public List<Invoice> getAll() {
    return new ArrayList<>(invoices
        .values());
  }

  @Override
  public Invoice update(int id, Invoice updateInvoice) {
    boolean isInvoiceNotInBase = !invoices.containsKey(id);
    return isInvoiceNotInBase ? null : invoices.put(id, updateInvoice);
  }

  @Override
  public Invoice delete(int id) {
    return invoices.remove(id);
  }

  public BigDecimal visit(Predicate<Invoice> rules, Function<InvoiceEntry, BigDecimal> entry) {

    return getAll()
        .stream()
        .filter(rules)
        .map(value -> value.getListOfInvoice()
            .stream()
            .map(entry)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
        )
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
