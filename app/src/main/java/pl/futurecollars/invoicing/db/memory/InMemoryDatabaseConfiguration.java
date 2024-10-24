package pl.futurecollars.invoicing.db.memory;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Configuration
@Slf4j
@ConditionalOnProperty(value = "invoicing-system.dataBase", havingValue = "inMemoryBase")
public class InMemoryDatabaseConfiguration {

  @Bean
  public Database<Invoice> inMemoryDatabaseInvoices() {
    log.info("Create in memory base of invoice");
    Long nextId = 1L;
    Map<Long, Invoice> invoices = new HashMap<>();
    return new InMemoryDatabase<>(nextId, invoices);
  }

  @Bean
  public Database<Company> inMemoryDatabaseCompanies() {
    log.info("Create in memory base of companies");
    Long nextId = 1L;
    Map<Long, Company> companies = new HashMap<>();
    return new InMemoryDatabase<>(nextId, companies);
  }
}
