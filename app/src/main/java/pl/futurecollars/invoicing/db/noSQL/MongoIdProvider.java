package pl.futurecollars.invoicing.db.noSQL;

import com.mongodb.client.MongoCollection;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

@RequiredArgsConstructor
public class MongoIdProvider {

  private static final String LAST_VALUE_KEY = "lastValue";
  private static final String ID_VALUE = "invoiceCounter";
  private static final String ID_KEY = "_id";

  private static final Document FILTER_DOCUMENT = new Document(ID_KEY, ID_VALUE);
  private final MongoCollection<Document> collection;
  private long lastValue = 0;
  
  @PostConstruct
  private void postConstruct() {
    var iterator = collection.find(FILTER_DOCUMENT).iterator();

    if (iterator.hasNext()) {
      lastValue = (long) iterator.next().get(LAST_VALUE_KEY);
      System.out.println("mongo id last value if 1 : " + lastValue);
    } else {
      collection.insertOne(counterDocument(0L));
      System.out.println("mongo id last value if 2 : " + lastValue);
    }
  }

  private Document counterDocument(long value) {
    Document document = new Document();
    document.append(ID_KEY, ID_VALUE);
    document.append(LAST_VALUE_KEY, value);
    return document;
  }


  public long getNextIdAndIncrement() {
    collection.findOneAndReplace(FILTER_DOCUMENT, counterDocument(++lastValue));
    System.out.println("mongo id getNextIdAndIncrement: " + lastValue);
return lastValue;
  }
}