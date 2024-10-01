package pl.futurecollars.invoicing.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.file.FileBasedDataBase;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
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
      @Value("${application.dataBase.filePath.base}") String fileBasePath,
      @Value("${application.dataBase.filePath.invoiceId}") String lastInvoiceIdFilePAth) {
    return new InvoiceSetup(fileBasePath, lastInvoiceIdFilePAth);
  }

  @Bean
  @ConditionalOnProperty(value = "application.dataBase", havingValue = "sqlBase")
  public Database sqlDatabase(JdbcTemplate jdbcTemplate) {
    log.info("Create SQL Base ");
    return new SqlDatabase(jdbcTemplate);
  }

  @ConditionalOnProperty(value = "application.dataBase", havingValue = "fileBase")
  @Bean
  public Database fileBaseDataBase(InvoiceSetup invoiceSetup) {
    log.info("Create file base");
    return new FileBasedDataBase(invoiceSetup);
  }

  @ConditionalOnProperty(value = "application.dataBase", havingValue = "inMemoryBase")
  @Bean
  public Database inMemoryDatabase() {
    log.info("Create in memory base");
    int nextId = 1;
    Map<Integer, Invoice> invoices = new HashMap<>();
    return new InMemoryDatabase(nextId, invoices);
  }

  @Bean
  public Invoice invoice() {
    return new Invoice(0, "", LocalDate.now(), new Company(), new Company(), new ArrayList<>() {
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

}

