package pl.futurecollars.invoicing.db.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import pl.futurecollars.invoicing.model.Company;

@AllArgsConstructor
@Slf4j
public class SqlDatabaseCommon {

  protected JdbcTemplate jdbcTemplate;

  public GeneratedKeyHolder saveCompany(Company company) {

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement preparedStatement =
          connection.prepareStatement(
              "insert into company"
                  + " (name, address, tax_identification,health_insurance_base_value,"
                  + "pension_insurance, amount_of_health_insurance, amount_of_health_insurance_to_reduce_tax) "
                  + "values (?,?,?,?,?,?,?);", new String[] {"id"});
      preparedStatement.setString(1, company.getName());
      preparedStatement.setString(2, company.getAddress());
      preparedStatement.setString(3, company.getTaxIdentification());
      preparedStatement.setBigDecimal(4, company.getHealthInsuranceBaseValue());
      preparedStatement.setBigDecimal(5, company.getPensionInsurance());
      preparedStatement.setBigDecimal(6, company.getAmountOfHealthInsurance());
      preparedStatement.setBigDecimal(7, company.getAmountOfHealthInsuranceToReduceTax());
      return preparedStatement;
    }, keyHolder);
    return keyHolder;
  }

  public void updateCompany(Long companyKey, Company company) {

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "update company c "
              + "set tax_identification = ?, name = ?, address = ?, health_insurance_base_value = ?, "
              + "pension_insurance = ?, amount_of_health_insurance = ?, amount_of_health_insurance_to_reduce_tax = ? "
              + "where c.id = " + companyKey);
      ps.setString(1, company.getTaxIdentification());
      ps.setString(2, company.getName());
      ps.setString(3, company.getAddress());
      ps.setBigDecimal(4, company.getHealthInsuranceBaseValue());
      ps.setBigDecimal(5, company.getPensionInsurance());
      ps.setBigDecimal(6, company.getAmountOfHealthInsurance());
      ps.setBigDecimal(7, company.getAmountOfHealthInsuranceToReduceTax());
      return ps;
    });
  }

  public Company buildCompany(ResultSet resultSet, String sellerOrBuyer) {
    Company company = new Company();
    try {
      company = Company.builder()
          .id(resultSet.getLong(sellerOrBuyer + "id"))
          .name(resultSet.getString(sellerOrBuyer + "name"))
          .taxIdentification(resultSet.getString(sellerOrBuyer + "tax_identification"))
          .address(resultSet.getString(sellerOrBuyer + "address"))
          .healthInsuranceBaseValue(resultSet.getBigDecimal(sellerOrBuyer + "health_insurance_base_value"))
          .pensionInsurance(resultSet.getBigDecimal(sellerOrBuyer + "pension_insurance"))
          .amountOfHealthInsurance(resultSet.getBigDecimal(sellerOrBuyer + "amount_of_health_insurance"))
          .amountOfHealthInsuranceToReduceTax(resultSet.getBigDecimal(sellerOrBuyer + "amount_of_health_insurance_to_reduce_tax"))
          .build();

    } catch (SQLException e) {
      log.warn(e.getMessage());
    }
    return company;
  }
}
