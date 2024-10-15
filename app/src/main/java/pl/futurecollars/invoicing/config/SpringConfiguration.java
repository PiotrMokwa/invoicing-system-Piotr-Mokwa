package pl.futurecollars.invoicing.config;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.file.FileBasedDataBase;
import pl.futurecollars.invoicing.db.jpa.InvoiceRepository;
import pl.futurecollars.invoicing.db.jpa.JpaDatabase;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.db.nosql.MongoBaseDatabase;
import pl.futurecollars.invoicing.db.nosql.MongoIdProvider;
import pl.futurecollars.invoicing.db.sql.SqlDatabase;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.setup.InvoiceSetup;

@Slf4j
@Configuration
public class SpringConfiguration {

  @Bean
  public InvoiceSetup invoiceSetup(
      @Value("${invoicing-system.dataBase.filePath.base}") String fileBasePath,
      @Value("${invoicing-system.dataBase.filePath.invoiceId}") String lastInvoiceIdFilePAth) {
    return new InvoiceSetup(fileBasePath, lastInvoiceIdFilePAth);
  }

  @Bean
  @ConditionalOnProperty(value = "invoicing-system.dataBase", havingValue = "sqlBase")
  public Database sqlDatabase(JdbcTemplate jdbcTemplate) {
    log.info("Create SQL Base ");
    return new SqlDatabase(jdbcTemplate);
  }

  @ConditionalOnProperty(value = "invoicing-system.dataBase", havingValue = "fileBase")
  @Bean
  public Database fileBaseDataBase(InvoiceSetup invoiceSetup) {
    log.info("Create file base");
    return new FileBasedDataBase(invoiceSetup);
  }

  @ConditionalOnProperty(value = "invoicing-system.dataBase", havingValue = "inMemoryBase")
  @Bean
  public Database inMemoryDatabase() {
    log.info("Create in memory base");
    Long nextId = 1L;
    Map<Integer, Invoice> invoices = new HashMap<>();
    return new InMemoryDatabase(nextId, invoices);
  }

  @Bean
  public Invoice invoice() {
    return new Invoice(0L, "", LocalDate.now(), new Company(), new Company(), new ArrayList<>() {
    });
  }

  @Bean
  public InvoiceEntry invoiceEntry() {
    return InvoiceEntry
        .builder().description("")
        .quantity(BigDecimal.ZERO)
        .price(BigDecimal.ZERO)
        .vatValue(BigDecimal.ZERO)
        .vatRate(null)
        .expansForCar(null)
        .build();
  }

  @Bean
  public Car car() {
    return Car.builder()
        .isPrivateUse(false)
        .carRegistrationNumber("")
        .build();
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.dataBase", havingValue = "jpa")
  public Database jpaDatabase(InvoiceRepository invoiceRepository) {

    log.info("Create jpa base");
    return new JpaDatabase(invoiceRepository);
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.dataBase", havingValue = "mongoDB")
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

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.dataBase", havingValue = "mongoDB")
  public MongoIdProvider mongoIdProvider(
      @Value("${invoicing-system.dataBase.counter.collection}") String collectionName,
      MongoDatabase mongoDatabase
  ) {
    MongoCollection<Document> invoiceIdCollection = mongoDatabase.getCollection(collectionName);
    return new MongoIdProvider(invoiceIdCollection);
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.dataBase", havingValue = "mongoDB")
  public Database mongoInvoicesDataBase(
      @Value("${invoicing-system.dataBase.collection}") String collectionName,
      MongoDatabase mongoDatabase,
      MongoIdProvider mongoIdProvider
  ) {
    MongoCollection<Invoice> invoicesCollection = mongoDatabase.getCollection(collectionName, Invoice.class);
    return MongoBaseDatabase.builder()
        .invoicesCollection(invoicesCollection)
        .mongoIdProvider(mongoIdProvider)
        .build();
  }
}

