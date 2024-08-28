package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class InMemoryDatabase implements Database {

  private int nextId;
  private final Map<Integer, Invoice> invoices;

  @Override
  public int save(Invoice invoice) {

    int addedInvoiceId = this.nextId;
    invoice.setId(addedInvoiceId);
    invoices.put(addedInvoiceId, invoice);
    this.nextId++;
    return addedInvoiceId;
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
  public Optional<Invoice> update(int id, Invoice updateInvoice) {

    boolean isInvoiceNotInBase = !invoices.containsKey(id);
    Invoice invoice = isInvoiceNotInBase ? null : invoices.put(id, updateInvoice);
    return Optional.ofNullable(invoice);
  }

  @Override
  public boolean delete(int id) {
    Invoice invoice = invoices.remove(id);
    Optional<Invoice> previusValue = Optional.ofNullable(invoice);
    return previusValue.isPresent();
  }
}
