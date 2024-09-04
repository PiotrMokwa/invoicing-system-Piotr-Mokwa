package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class InMemoryDatabase implements Database {

  private int nextId;
  private final Map<Integer, Invoice> invoices;

  @Override
  public int save(Invoice invoice) {
    invoice.setId(nextId);
    invoices.put(nextId, invoice);
    int addedInvoiceId = nextId;
    nextId++;
    boolean isInvoiceAdded = invoice.equals(getById(addedInvoiceId));
    return isInvoiceAdded ? addedInvoiceId : null;
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
}
