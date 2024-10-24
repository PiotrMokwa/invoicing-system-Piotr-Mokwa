package pl.futurecollars.invoicing.db.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Configuration
@Slf4j
@ConditionalOnProperty(value = "invoicing-system.dataBase", havingValue = "sqlBase")
public class SqlDatabaseConfiguration {

  @Bean
  public Database<Invoice> sqlDatabaseInvoice(JdbcTemplate jdbcTemplate) {
    log.info("Create invoice SQL Base ");
    return new InvoiceSqlDatabase(jdbcTemplate);
  }

  @Bean
  public Database<Company> sqlDatabaseCompany(JdbcTemplate jdbcTemplate) {
    log.info("Create company SQL Base ");
    return new CompanySqlDatabase(jdbcTemplate);
  }

}
