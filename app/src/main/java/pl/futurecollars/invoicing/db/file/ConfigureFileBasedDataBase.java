package pl.futurecollars.invoicing.db.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.setup.InvoiceSetup;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "invoicing-system.dataBase", havingValue = "fileBase")
public class ConfigureFileBasedDataBase {

  @Bean
  public Database<Invoice> fileBaseDataBaseInvoice(InvoiceSetup invoiceSetup) {
    log.info("Create invoice file base");
    return new FileBasedDataBase<>(invoiceSetup, Invoice.class);
  }

  @Bean
  public Database<Company> fileBaseDataBaseCompany(InvoiceSetup invoiceSetup) {
    log.info("Create company  file base");
    return new FileBasedDataBase<>(invoiceSetup, Company.class);
  }
}
