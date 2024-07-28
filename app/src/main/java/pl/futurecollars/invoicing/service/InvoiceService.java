package pl.futurecollars.invoicing.service;

import pl.futurecollars.invoicing.db.Database;

public class InvoiceService {

  private final Database database;

  public InvoiceService(Database database) {
    this.database = database;
  }

}
