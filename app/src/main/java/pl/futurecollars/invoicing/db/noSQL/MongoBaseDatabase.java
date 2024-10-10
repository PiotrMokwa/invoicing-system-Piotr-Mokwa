package pl.futurecollars.invoicing.db.noSQL;

import com.mongodb.client.MongoCollection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.Document;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@Builder
@AllArgsConstructor
@Data
public class MongoBaseDatabase implements Database {

  private MongoCollection<Invoice> invoicesCollection;
  public MongoIdProvider mongoIdProvider;


  private Document documentIdFilter(Long id){
    return new Document("_id",id);
  }
  @Override
  public Long save(Invoice invoice) {

    long nextId = mongoIdProvider.getNextIdAndIncrement();
    invoice.setId(nextId);
    invoicesCollection.insertOne(invoice);
    return nextId;
  }

  @Override
  public Invoice getById(Long id) {

    return invoicesCollection.find(documentIdFilter(id)).first();
  }

  @Override
  public List<Invoice> getAll() {
    return StreamSupport.stream(invoicesCollection.find().spliterator(),false)
        .collect(Collectors.toList());

  }

  @Override
  public Invoice update(Long id, Invoice updateInvoice) {

    updateInvoice.setId(id);
    System.out.println("invoice id" + updateInvoice.getId());
    return invoicesCollection.findOneAndReplace(documentIdFilter(id),updateInvoice);
  }

  @Override
  public Invoice delete(Long id) {
    
    return invoicesCollection.findOneAndDelete(documentIdFilter(id));
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
        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.UP);
  }
}
