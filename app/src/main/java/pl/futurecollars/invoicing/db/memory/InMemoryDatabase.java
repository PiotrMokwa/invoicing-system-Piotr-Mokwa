package pl.futurecollars.invoicing.db.memory;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

public class InMemoryDatabase implements Database {

  @Override
  public boolean save(Invoice invoice) {
    return false;
  }

  @Override
  public Optional<Invoice> getById(int id) {
    return Optional.empty();
  }

  @Override
  public List<Invoice> getALL() {
    return List.of();
  }

  @Override
  public void update(int id, Invoice updateInvoice) {

  }

  @Override
  public boolean delete(int id) {
    return false;
  }
}
