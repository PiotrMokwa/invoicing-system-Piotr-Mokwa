package pl.futurecollars.invoicing.db.nosql;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "invoicing-system.dataBase", havingValue = "mongoDB")
public class MongoBaseDatabaseConfiguration {

  @Bean
  public Database<Invoice> mongoInvoicesDataBase(
      @Value("${invoicing-system.dataBase.collection.invoices}") String collectionName,
      MongoDatabase mongoDatabase,
      MongoIdProvider mongoIdProvider
  ) {
    MongoCollection<Invoice> invoicesCollection = mongoDatabase.getCollection(collectionName, Invoice.class);
    return new MongoBaseDatabase<>(mongoIdProvider, invoicesCollection);

  }

  @Bean
  public Database<Company> mongoCompanyDataBase(
      @Value("${invoicing-system.dataBase.collection.companies}") String collectionName,
      MongoDatabase mongoDatabase,
      MongoIdProvider mongoIdProvider
  ) {
    MongoCollection<Company> invoicesCollection = mongoDatabase.getCollection(collectionName, Company.class);
    return new MongoBaseDatabase<>(mongoIdProvider, invoicesCollection);

  }

  @Bean
  public MongoIdProvider mongoIdProvider(
      @Value("${invoicing-system.dataBase.counter.collection}") String collectionName,
      MongoDatabase mongoDatabase
  ) {
    MongoCollection<Document> invoiceIdCollection = mongoDatabase.getCollection(collectionName);
    return new MongoIdProvider(invoiceIdCollection);
  }

  @Bean
  public MongoDatabase mongoDatabase(
      @Value("${invoicing-system.dataBase.name}") String databaseName

  ) {
    log.info("Create mongo base");
    CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
        fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    MongoClientSettings settings = MongoClientSettings.builder()
        .codecRegistry(pojoCodecRegistry)
        .build();

    MongoClient client = MongoClients.create(settings);
    return client.getDatabase(databaseName);
  }
}
