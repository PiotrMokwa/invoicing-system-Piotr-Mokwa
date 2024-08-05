package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

public class InMemoryDatabase implements Database {

  private @Getter int nextId;
  private final Map<Integer, Invoice> invoices;

  public InMemoryDatabase(Integer nextId, Map<Integer, Invoice> invoices) {
    this.nextId = nextId;
    this.invoices = invoices;
  }

  @Override
  public boolean save(Invoice invoice) {
    Invoice beforeInvoice;
    invoice.setId(nextId);
    beforeInvoice = invoices.put(nextId, invoice);
    nextId++;

    return Optional
        .ofNullable(beforeInvoice)
        .isEmpty();
  }

  @Override
  public Optional<Invoice> getById(int id) {
    return Optional.ofNullable(invoices.get(id));
  }

  @Override
  public List<Invoice> getAll() {
    return new ArrayList<>(invoices
        .values());
  }

  @Override
  public void update(int id, Invoice updateInvoice) {
    invoices.put(id, updateInvoice);
  }

  @Override
  public boolean delete(int id) {
    Optional<Invoice> deletedInvoice = Optional
        .ofNullable(invoices.remove(id));
    return deletedInvoice.isPresent();
  }
}
