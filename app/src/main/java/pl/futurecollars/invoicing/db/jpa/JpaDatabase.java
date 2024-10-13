package pl.futurecollars.invoicing.db.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@AllArgsConstructor
public class JpaDatabase implements Database {

  private InvoiceRepository invoiceRepository;

  @Override
  public Long save(Invoice invoice) {
    Invoice addedInvoice = null;
    try {

      addedInvoice = invoiceRepository.save(invoice);
      System.out.println(addedInvoice);
    } catch (IllegalArgumentException | OptimisticLockingFailureException iae) {
      System.out.println(iae);
    }

    return addedInvoice.getId();
  }

  @Override
  public Invoice getById(Long id) {
    return invoiceRepository.findById(id).orElse(null);
  }

  @Override
  public List<Invoice> getAll() {
    return StreamSupport.stream(invoiceRepository.findAll().spliterator(), false).collect(Collectors.toList());
  }

  @Override
  public Invoice update(Long id, Invoice updateInvoice) {
    Invoice oldInvoice = getById(id);
    updateInvoice.setId(id);
    updateInvoice.getBuyer().setId(oldInvoice.getBuyer().getId());
    updateInvoice.getSeller().setId(oldInvoice.getSeller().getId());
    invoiceRepository.save(updateInvoice);
    return oldInvoice;
  }

  @Override
  public Invoice delete(Long id) {
    Invoice deletedInvoice = getById(id);
    invoiceRepository.delete(getById(id));

    return deletedInvoice;
  }

  @Override
  public BigDecimal visit(Predicate<Invoice> rules, Function<InvoiceEntry, BigDecimal> entry) {

    return getAll()
        .stream()
        .filter(rules)
        .map(value -> value.getListOfInvoiceEntry()
            .stream()
            .map(entry)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
        )
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}



