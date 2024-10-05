package pl.futurecollars.invoicing.db.sql;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@Data
@Slf4j
@AllArgsConstructor
public class SqlDatabase implements Database {

  private final JdbcTemplate jdbcTemplate;

  @Override
  @Transactional
  public Long save(Invoice invoice) {
    log.info(invoice.toString());
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement preparedStatement =
          connection.prepareStatement(
              "insert into company"
                  + " (name, address, tax_identification,health_insurance_base_value,"
                  + "pension_insurance, amount_of_health_insurance, amount_of_health_insurance_to_reduce_tax) "
                  + "values (?,?,?,?,?,?,?);", new String[] {"id"});
      preparedStatement.setString(1, invoice.getBuyer().getName());
      preparedStatement.setString(2, invoice.getBuyer().getAddress());
      preparedStatement.setString(3, invoice.getBuyer().getTaxIdentification());
      preparedStatement.setBigDecimal(4, invoice.getBuyer().getHealthInsuranceBaseValue());
      preparedStatement.setBigDecimal(5, invoice.getBuyer().getPensionInsurance());
      preparedStatement.setBigDecimal(6, invoice.getBuyer().getAmountOfHealthInsurance());
      preparedStatement.setBigDecimal(7, invoice.getBuyer().getAmountOfHealthInsuranceToReduceTax());
      return preparedStatement;
    }, keyHolder);
    long buyerId = keyHolder.getKey().longValue();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection
          .prepareStatement("insert into company "
              +
              "(tax_identification,name, address,health_insurance_base_value,pension_insurance,"
              + " amount_of_health_insurance, amount_of_health_insurance_to_reduce_tax) "
              + "values (?,?,?,?,?,?,?);", new String[] {"id"});
      ps.setString(1, invoice.getSeller().getTaxIdentification());
      ps.setString(2, invoice.getSeller().getName());
      ps.setString(3, invoice.getSeller().getAddress());
      ps.setBigDecimal(4, invoice.getSeller().getHealthInsuranceBaseValue());
      ps.setBigDecimal(5, invoice.getSeller().getPensionInsurance());
      ps.setBigDecimal(6, invoice.getSeller().getAmountOfHealthInsurance());
      ps.setBigDecimal(7, invoice.getSeller().getAmountOfHealthInsuranceToReduceTax());
      return ps;
    }, keyHolder);

    long sellerId = Objects.requireNonNull(keyHolder.getKey()).longValue();

    jdbcTemplate.update(connection -> {
      PreparedStatement preparedStatement = connection
          .prepareStatement("insert into invoice (date,number,seller,buyer) values (?,?,?,?);", new String[] {"id"});
      preparedStatement.setDate(1, Date.valueOf(invoice.getDate()));
      preparedStatement.setString(2, invoice.getNumber());
      preparedStatement.setLong(3, sellerId);
      preparedStatement.setLong(4, buyerId);
      return preparedStatement;
    }, keyHolder);

    int invoiceId = keyHolder.getKey().intValue();

    invoice.getListOfInvoiceEntry().forEach(invoiceEntry -> {
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection
            .prepareStatement("insert into invoice_entry (description, quantity, price, vat_value, vat_rate, expanse_for_car) values (?,?,?,?,?,?)",
                new String[] {"id"});
        ps.setString(1, invoiceEntry.getDescription());
        ps.setBigDecimal(2, invoiceEntry.getQuantity());
        ps.setBigDecimal(3, invoiceEntry.getPrice());
        ps.setBigDecimal(4, invoiceEntry.getVatValue());
        ps.setString(5, invoiceEntry.getVatRate().name());
        ps.setLong(6, insertCarReturnId(invoiceEntry, keyHolder));
        return ps;
      }, keyHolder);

      long keyInvoiceEntry = keyHolder.getKey().longValue();

      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection
            .prepareStatement("insert into invoice_invoice_entry (invoice_id,invoice_entry_id) values (?,?);");
        ps.setInt(1, invoiceId);
        ps.setLong(2, keyInvoiceEntry);
        return ps;
      });
    });
    return Integer.valueOf(invoiceId).longValue();
  }

  @Override
  public Invoice getById(Long id) {

    return jdbcTemplate.query(askSqlForGetAllInvoice() + " where i.id = " + id, rse -> {
      if (!rse.next()) {
        return null;
      } else {
        Long invoiceId = rse.getLong("id");
        return buildInvoice(rse, invoiceId);
      }
    });
  }

  public String askSqlForGetAllInvoice() {

    return "select i.id, i.date ,i.number, "
        + "c1.id as seller_id,"
        + "c1.name as seller_name, "
        + "c1.tax_identification as seller_tax_identification, "
        + "c1.address as seller_address, "
        + "c1.health_insurance_base_value as seller_health_insurance_base_value, "
        + "c1.pension_insurance as seller_pension_insurance, "
        + "c1.amount_of_health_insurance as seller_amount_of_health_insurance, "
        + "c1.amount_of_health_insurance_to_reduce_tax as seller_amount_of_health_insurance_to_reduce_tax, "
        + "c2.id as buyer_id,"
        + "c2.name as buyer_name, "
        + "c2.tax_identification as buyer_tax_identification, "
        + "c2.address as buyer_address, "
        + "c2.health_insurance_base_value as buyer_health_insurance_base_value, "
        + "c2.pension_insurance as buyer_pension_insurance, "
        + "c2.amount_of_health_insurance as buyer_amount_of_health_insurance, "
        + "c2.amount_of_health_insurance_to_reduce_tax as buyer_amount_of_health_insurance_to_reduce_tax "
        + "from invoice i "
        + "inner join company c1 on i.seller = c1.id "
        + "inner join company c2 on i.buyer = c2.id ";
  }

  public List<InvoiceEntry> readSqlInvoiceEntry(Long invoiceId) {

    return jdbcTemplate.query("select * from invoice_invoice_entry iie "
            + "inner join invoice_entry ie on ie.id = iie.invoice_entry_id "
            + "left outer join car c on ie.expanse_for_car = c.id "
            + "where iie.invoice_id = " + invoiceId,
        (entryRow, empty) -> InvoiceEntry.builder()
            .description(entryRow.getString("description"))
            .quantity(entryRow.getBigDecimal("quantity"))
            .price(entryRow.getBigDecimal("price"))
            .vatValue(entryRow.getBigDecimal("vat_value"))
            .vatRate(Vat.valueOf(entryRow.getString("vat_rate")))
            .expansForCar(Car.builder()
                .carRegistrationNumber(entryRow.getString("car_registration_number"))
                .isPrivateUse(entryRow.getBoolean("is_private_use"))
                .build())
            .build()
    );
  }

  public Invoice buildInvoice(ResultSet resultSet, Long invoiceId) {
    Invoice invoice = new Invoice();
    try {
      invoice = Invoice.builder()
          .id(invoiceId)
          .date(resultSet.getDate("date").toLocalDate())
          .number(resultSet.getString("number"))
          .buyer(
              Company.builder()
                  .id(resultSet.getLong("buyer_id"))
                  .name(resultSet.getString("buyer_name"))
                  .taxIdentification(resultSet.getString("buyer_tax_identification"))
                  .address(resultSet.getString("buyer_address"))
                  .healthInsuranceBaseValue(resultSet.getBigDecimal("buyer_health_insurance_base_value"))
                  .pensionInsurance(resultSet.getBigDecimal("buyer_pension_insurance"))
                  .amountOfHealthInsurance(resultSet.getBigDecimal("buyer_amount_of_health_insurance"))
                  .amountOfHealthInsuranceToReduceTax(resultSet.getBigDecimal("buyer_amount_of_health_insurance_to_reduce_tax"))
                  .build())
          .seller(Company.builder()
              .id(resultSet.getLong("seller_id"))
              .name(resultSet.getString("seller_name"))
              .taxIdentification(resultSet.getString("seller_tax_identification"))
              .address(resultSet.getString("seller_address"))
              .healthInsuranceBaseValue(resultSet.getBigDecimal("seller_health_insurance_base_value"))
              .pensionInsurance(resultSet.getBigDecimal("seller_pension_insurance"))
              .amountOfHealthInsurance(resultSet.getBigDecimal("seller_amount_of_health_insurance"))
              .amountOfHealthInsuranceToReduceTax(resultSet.getBigDecimal("seller_amount_of_health_insurance_to_reduce_tax"))
              .build())
          .listOfInvoiceEntry(readSqlInvoiceEntry(invoiceId))
          .build();
    } catch (SQLException e) {
      log.warn(e.getMessage());
    }
    return invoice;
  }

  @Override
  public List<Invoice> getAll() {

    return jdbcTemplate.query(
        askSqlForGetAllInvoice() + "order by id ", (rs, rowNr) -> {
          Long invoiceId = rs.getLong("id");
          return buildInvoice(rs, invoiceId);
        });
  }

  private Integer insertCarReturnId(InvoiceEntry invoiceEntry, GeneratedKeyHolder generatedKeyHolder) {

    jdbcTemplate.update(connection -> {
      PreparedStatement preparedStatement = connection
          .prepareStatement("insert into car (is_private_use,car_registration_number) values (?,?);", new String[] {"id"});
      preparedStatement.setBoolean(1, invoiceEntry.getExpansForCar().isPrivateUse());
      preparedStatement.setString(2, invoiceEntry.getExpansForCar().getCarRegistrationNumber());
      return preparedStatement;
    }, generatedKeyHolder);
    return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
  }

  @Override
  public Invoice update(Long id, Invoice updateInvoice) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(
              " update invoice i "
                  + "set date = ?, number = ? "
                  + "where i.id = " + id, new String[] {"seller", "buyer"});
          ps.setDate(1, Date.valueOf(updateInvoice.getDate()));
          ps.setString(2, updateInvoice.getNumber());
          return ps;
        }, keyHolder);
    Map<String, Object> companyKeys = keyHolder.getKeys();
    updateCompany(companyKeys.get("seller"), updateInvoice.getSeller());
    updateCompany(companyKeys.get("buyer"), updateInvoice.getBuyer());
    deleteEntries(id);
    addEntries(id, updateInvoice.getListOfInvoiceEntry());
    return null;
  }

  public void addEntries(Long invoiceId, List<InvoiceEntry> list) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    list.forEach(invoiceEntry -> {
      jdbcTemplate.update(coonection -> {
        PreparedStatement ps = coonection.prepareStatement(
            "insert into invoice_entry  "
                + "(description,quantity,price,vat_value,vat_rate,expanse_for_car) "
                + "values(?,?,?,?,?,?) ", new String[] {"id"});
        ps.setString(1, invoiceEntry.getDescription());
        ps.setBigDecimal(2, invoiceEntry.getQuantity());
        ps.setBigDecimal(3, invoiceEntry.getPrice());
        ps.setBigDecimal(4, invoiceEntry.getVatValue());
        ps.setString(5, invoiceEntry.getVatRate().name());
        ps.setLong(6, insertCarReturnId(invoiceEntry, keyHolder));
        return ps;
      }, keyHolder);
      int invoiceEntryId = keyHolder.getKey().intValue();

      jdbcTemplate.update(coonection -> {
        PreparedStatement ps = coonection.prepareStatement(
            "insert into invoice_invoice_entry  (invoice_id,invoice_entry_id) values(?,?) ");
        ps.setLong(1, invoiceId);
        ps.setLong(2, invoiceEntryId);
        return ps;
      });
    });
  }

  public void deleteEntries(Long invoiceId) {
    jdbcTemplate.update(connection -> {

      PreparedStatement ps = connection.prepareStatement(
          "delete from invoice_invoice_entry where invoice_id = ?");
      ps.setLong(1, invoiceId);
      return ps;
    });
  }

  public Invoice deleteInvoice(Long id) {

    Invoice deletedInvoice = getById(id);
    jdbcTemplate.update("delete from invoice i where i.id =  " + id);
    return deletedInvoice;
  }

  public void updateCompany(Object companyKey, Company company) {

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

  @Override
  public Invoice delete(Long id) {

    deleteInvoice(id);
    return deleteInvoice(id);
  }

  @Override
  public BigDecimal visit(Predicate<Invoice> rules, Function<InvoiceEntry, BigDecimal> entry) {
    return getAll()
        .stream()
        .filter(rules)
        .map(value -> value.getListOfInvoiceEntry()
            .stream()
            .map(entry)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
        )
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
