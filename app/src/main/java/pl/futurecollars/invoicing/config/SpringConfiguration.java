package pl.futurecollars.invoicing.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
}

