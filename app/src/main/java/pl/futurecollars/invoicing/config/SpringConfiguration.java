package pl.futurecollars.invoicing.config;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.file.FileBasedDataBase;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.model.Insurance;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.setup.InvoiceSetup;

@Slf4j
@Configuration
public class SpringConfiguration {

  private final BigDecimal healthInsuranceBaseValue = BigDecimal.valueOf(3554.89);
  private final BigDecimal pensionInsurance = BigDecimal.valueOf(514.57);

  @Bean
  public Insurance insurance() {
    return new Insurance(healthInsuranceBaseValue, pensionInsurance);
  }

  @Bean
  public InvoiceSetup invoiceSetup(
      @Value("${application.dataBase.filePath.base}") String fileBasePath,
      @Value("${application.dataBase.filePath.invoiceId}") String lastInvoiceIdFilePAth) {
    return new InvoiceSetup(fileBasePath, lastInvoiceIdFilePAth);
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
}
