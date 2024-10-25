package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import net.minidev.json.annotate.JsonIgnore;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.WithId;

@Builder
@AllArgsConstructor
public class InMemoryDatabase<T extends WithId> implements Database<T> {

  @JsonIgnore
  private Long nextId;
  private final Map<Long, T> items;

  public InMemoryDatabase(T nextId) {
    this.items = new HashMap<Long, T>();
  }

  @Override
  public Long save(T item) {
    item.setId(nextId);

    items.put(nextId, item);
    return nextId++;
  }

  @Override
  public T getById(Long id) {
    return items.get(id);
  }

  @Override
  public List<T> getAll() {
    return new ArrayList<>(items
        .values());
  }

  @Override
  public T update(Long id, T item) {
    boolean isInvoiceNotInBase = !items.containsKey(id);
    return isInvoiceNotInBase ? null : items.put(id, item);
  }

  @Override
  public T delete(Long id) {
    return items.remove(id);
  }

  //  public BigDecimal visit(Predicate<T> rules, Function<InvoiceEntry, BigDecimal> entry) {
  //
  //    return getAll()
  //        .stream()
  //        .filter(rules)
  //        .map(value -> value.getListOfInvoiceEntry()
  //            .stream()
  //            .map(entry)
  //            .reduce(BigDecimal.ZERO, BigDecimal::add)
  //        )
  //        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.UP);
  //  }
}
