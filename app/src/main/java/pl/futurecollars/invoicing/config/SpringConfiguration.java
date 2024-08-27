package pl.futurecollars.invoicing.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.InvoiceSetup;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.file.FileBasedDataBase;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.model.Invoice;

@Configuration
public class SpringConfiguration {

  private static final String FILE_BASE_PATH = "Base.txt";
  private static final String LAST_FILE_PATH = "Id.txt";

  @Bean
  public InvoiceSetup invoiceSetup() {
    try {
      Files.createFile(Path.of(FILE_BASE_PATH));
    } catch (IOException exception) {
      exception.getStackTrace();
    }
    try {
      Files.createFile(Path.of(LAST_FILE_PATH));
    } catch (IOException exception) {
      exception.getStackTrace();
    }

    return new InvoiceSetup(FILE_BASE_PATH, LAST_FILE_PATH);
  }

  @Bean
  public Database fileBaseDataBase(InvoiceSetup invoiceSetup) {
    return new FileBasedDataBase(invoiceSetup);
  }

  @Bean
  public Database inMemoryDatabase() {
    int nextId = 0;
    Map<Integer, Invoice> invoices = new HashMap<>();
    return new InMemoryDatabase(nextId, invoices);
  }
}
