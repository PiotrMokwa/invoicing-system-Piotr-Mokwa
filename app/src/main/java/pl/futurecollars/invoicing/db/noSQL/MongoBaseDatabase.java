package pl.futurecollars.invoicing.db.nosql;

import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.Document;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.WithId;

@Builder
@AllArgsConstructor
@Data
public class MongoBaseDatabase<T extends WithId> implements Database<T> {

  public MongoIdProvider mongoIdProvider;
  private MongoCollection<T> invoicesCollection;

  private Document documentIdFilter(Long id) {

    return new Document("_id", id);
  }

  @Override
  public Long save(T item) {

    long nextId = mongoIdProvider.getNextIdAndIncrement();
    item.setId(nextId);
    invoicesCollection.insertOne(item);
    return nextId;
  }

  @Override
  public T getById(Long id) {

    return invoicesCollection.find(documentIdFilter(id)).first();
  }

  @Override
  public List<T> getAll() {
    return StreamSupport.stream(invoicesCollection.find().spliterator(), false)
        .collect(Collectors.toList());

  }

  @Override
  public T update(Long id, T updatedItem) {

    updatedItem.setId(id);
    System.out.println("invoice id" + updatedItem.getId());
    return invoicesCollection.findOneAndReplace(documentIdFilter(id), updatedItem);
  }

  @Override
  public T delete(Long id) {

    return invoicesCollection.findOneAndDelete(documentIdFilter(id));
  }

}
