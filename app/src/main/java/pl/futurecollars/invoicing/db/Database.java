package pl.futurecollars.invoicing.db;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.model.Invoice;

public interface Database {
  boolean save(Invoice invoice);
  Optional<Invoice> getById(int id);
  List<Invoice> getALL();
  void update(int id, Invoice updateInvoice);
  boolean delete(int id);
}
