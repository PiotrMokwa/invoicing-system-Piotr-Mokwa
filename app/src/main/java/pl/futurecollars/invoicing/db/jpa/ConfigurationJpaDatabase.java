package pl.futurecollars.invoicing.db.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "invoicing-system.dataBase", havingValue = "jpa")
public class ConfigurationJpaDatabase {

  @Bean
  public Database<Invoice> jpaDatabaseInvoice(InvoiceRepository invoiceRepository) {

    log.info("Create jpa invoice base");
    return new JpaDatabase<>(invoiceRepository);
  }

  @Bean
  public Database<Company> jpaDatabaseCompany(CompanyRepository invoiceRepository) {

    log.info("Create jpa company base");
    return new JpaDatabase<>(invoiceRepository);

  }
}
