package pl.futurecollars.invoicing.db;

import java.util.List;
import pl.futurecollars.invoicing.model.Invoice;

public interface Database {

  int save(Invoice invoice);

  Invoice getById(int id);

  List<Invoice> getAll();

  Invoice update(int id, Invoice updateInvoice);

  Invoice delete(int id);

}
