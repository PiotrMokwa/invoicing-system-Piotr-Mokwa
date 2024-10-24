package pl.futurecollars.invoicing.db;

import java.util.List;

public interface Database<T> {

  Long save(T object);

  T getById(Long id);

  List<T> getAll();

  T update(Long id, T updateObject);

  T delete(Long id);


  //  BigDecimal visit(Predicate<T> rules, Function<InvoiceEntry, BigDecimal> entry);

}
