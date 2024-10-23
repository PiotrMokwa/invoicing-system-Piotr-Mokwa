package pl.futurecollars.invoicing.db.sql;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;

public class CompanySqlDatabase extends SqlDatabaseCommon implements Database<Company> {

  public CompanySqlDatabase(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }

  @Override
  public Long save(Company object) {
    return saveCompany(object).getKey().longValue();
  }

  public String askSqlForGetAllCompanies() {

    return "select "
        + "c.id, "
        + "c.tax_identification, "
        + "c.name, "
        + "c.address, "
        + "c.health_insurance_base_value , "
        + "c.pension_insurance, "
        + "c.amount_of_health_insurance, "
        + "c.amount_of_health_insurance_to_reduce_tax "
        + "from company c ";

  }

  @Override
  public Company getById(Long id) {
    return jdbcTemplate.query(askSqlForGetAllCompanies() + " where i.id = " + id, rse -> {
      if (!rse.next()) {
        return null;
      } else {
        Long invoiceId = rse.getLong("id");
        return buildCompany(rse, "");
      }
    });
  }

  @Override
  public List<Company> getAll() {
    return jdbcTemplate.query(askSqlForGetAllCompanies() + "order by id ", (rs, rowNr) ->
        buildCompany(rs, ""));

  }

  @Override
  public Company update(Long id, Company updateObject) {
    Company oldCompany = getById(id);
    updateCompany(id, updateObject);
    return oldCompany;
  }

  @Override
  public Company delete(Long id) {
    Company deletedInvoice = getById(id);
    jdbcTemplate.update("delete from company c where c.id =  " + id);
    return deletedInvoice;
  }
}
