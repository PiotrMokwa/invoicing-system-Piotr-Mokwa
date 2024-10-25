package pl.futurecollars.invoicing.db.jpa;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.repository.CrudRepository;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.WithId;

@AllArgsConstructor
public class JpaDatabase<T extends WithId> implements Database<T> {

  private CrudRepository<T, Long> invoiceRepository;

  @Override
  public Long save(T item) {
    T addedInvoice = null;
    try {

      addedInvoice = invoiceRepository.save(item);
      System.out.println(addedInvoice);
    } catch (IllegalArgumentException | OptimisticLockingFailureException iae) {
      System.out.println(iae);
    }

    return addedInvoice.getId();
  }

  @Override
  public T getById(Long id) {
    return invoiceRepository.findById(id).orElse(null);
  }

  @Override
  public List<T> getAll() {
    return StreamSupport.stream(invoiceRepository.findAll().spliterator(), false).collect(Collectors.toList());
  }

  @Override
  public T update(Long id, T updatedItem) {
    T oldInvoice = getById(id);
    updatedItem.setId(id);
    invoiceRepository.save(updatedItem);
    return oldInvoice;
  }

  @Override
  public T delete(Long id) {
    T deletedInvoice = getById(id);
    invoiceRepository.delete(getById(id));

    return deletedInvoice;
  }

}
