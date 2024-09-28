package pl.futurecollars.invoicing.db.sql;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
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
  private final Map<Integer, Vat> vatToId = new HashMap<>();

  @PostConstruct
  public void initVatRatesMap() {

    Map<Integer, Vat> vatTable = new HashMap<>();
    jdbcTemplate.query("select * from vat",
        rs -> {
          vatToId.put(rs.getInt("id"), Vat.valueOf(rs.getString("name")));
        });
  }

  @Override
  @Transactional
  public int save(Invoice invoice) {
    log.info(invoice.toString());
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement preparedStatement =
          connection.prepareStatement(
              "insert into companies"
                  + " (name, address, tax_identification,healthInsuranceBaseValue,"
                  + "pensionInsurance, amountOfHealthInsurance, amountOfHealthInsuranceToReduceTax) "
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
          .prepareStatement("insert into companies "
              +
              "(tax_identification,name, address,healthInsuranceBaseValue,pensionInsurance,"
              + " amountOfHealthInsurance, amountOfHealthInsuranceToReduceTax) "
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
          .prepareStatement("insert into invoice (date,invoice_number,seller,buyer) values (?,?,?,?);", new String[] {"id"});
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
        ps.setInt(5, vatToId
            .entrySet()
            .stream()
            .filter(vatEntry -> vatEntry.getValue() == invoiceEntry.getVatRate())
            .collect(Collectors.toList()).get(0).getKey());
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
    return invoiceId;
  }

  @Override
  public Invoice getById(int id) {

    return jdbcTemplate.query(askSqlForGetAllInvoice() + " where i.id = " + id, rse -> {
      if (!rse.next()) {
        return null;
      } else {
        int invoiceId = rse.getInt("id");
        return buildInvoice(rse, invoiceId);
      }
    });
  }

  public String askSqlForGetAllInvoice() {

    return "select i.id, i.date ,i.invoice_number, "
        + "c1.id as seller_id,"
        + "c1.name as seller_name, "
        + "c1.tax_identification as seller_tax_identification, "
        + "c1.address as seller_address, "
        + "c1.healthinsurancebasevalue as seller_healthinsurancebasevalue, "
        + "c1.pensioninsurance as seller_pensioninsurance, "
        + "c1.amountofhealthinsurance as seller_amountofhealthinsurance, "
        + "c1.amountofhealthinsurancetoreducetax as seller_amountofhealthinsurancetoreducetax, "
        + "c2.id as buyer_id,"
        + "c2.name as buyer_name, "
        + "c2.tax_identification as buyer_tax_identification, "
        + "c2.address as buyer_address, "
        + "c2.healthinsurancebasevalue as buyer_healthinsurancebasevalue, "
        + "c2.pensioninsurance as buyer_pensioninsurance, "
        + "c2.amountofhealthinsurance as buyer_amountofhealthinsurance, "
        + "c2.amountofhealthinsurancetoreducetax as buyer_amountofhealthinsurancetoreducetax "
        + "from invoice i "
        + "inner join companies c1 on i.seller = c1.id "
        + "inner join companies c2 on i.buyer = c2.id ";
  }

  public List<InvoiceEntry> readSqlInvoiceEntry(int invoiceId) {

    return jdbcTemplate.query("select * from invoice_invoice_entry iie "
            + "inner join invoice_entry ie on ie.id = iie.invoice_entry_id "
            + "left outer join car c on ie.expanse_for_car = c.id "
            + "where iie.invoice_id = " + invoiceId,
        (entryRow, empty) -> InvoiceEntry.builder()
            .description(entryRow.getString("description"))
            .quantity(entryRow.getBigDecimal("quantity"))
            .price(entryRow.getBigDecimal("price"))
            .vatValue(entryRow.getBigDecimal("vat_value"))
            .vatRate(vatToId.get(entryRow.getInt("vat_rate")))
            .expansForCar(Car.builder()
                .carRegistrationNumber(entryRow.getString("car_registration_number"))
                .isPrivateUse(entryRow.getBoolean("is_private_use"))
                .build())
            .build()
    );
  }

  public Invoice buildInvoice(ResultSet resultSet, int invoiceId) {
    Invoice invoice = new Invoice();
    try {
      invoice = Invoice.builder()
          .id(invoiceId)
          .date(resultSet.getDate("date").toLocalDate())
          .number(resultSet.getString("invoice_number"))
          .buyer(
              Company.builder()
                  .id(resultSet.getString("buyer_id"))
                  .name(resultSet.getString("buyer_name"))
                  .taxIdentification(resultSet.getString("buyer_tax_identification"))
                  .address(resultSet.getString("buyer_address"))
                  .healthInsuranceBaseValue(resultSet.getBigDecimal("buyer_healthinsurancebasevalue"))
                  .pensionInsurance(resultSet.getBigDecimal("buyer_pensioninsurance"))
                  .amountOfHealthInsurance(resultSet.getBigDecimal("buyer_amountofhealthinsurance"))
                  .amountOfHealthInsuranceToReduceTax(resultSet.getBigDecimal("buyer_amountofhealthinsurancetoreducetax"))
                  .build())
          .seller(Company.builder()
              .id(resultSet.getString("seller_id"))
              .name(resultSet.getString("seller_name"))
              .taxIdentification(resultSet.getString("seller_tax_identification"))
              .address(resultSet.getString("seller_address"))
              .healthInsuranceBaseValue(resultSet.getBigDecimal("seller_healthinsurancebasevalue"))
              .pensionInsurance(resultSet.getBigDecimal("seller_pensioninsurance"))
              .amountOfHealthInsurance(resultSet.getBigDecimal("seller_amountofhealthinsurance"))
              .amountOfHealthInsuranceToReduceTax(resultSet.getBigDecimal("seller_amountofhealthinsurancetoreducetax"))
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
          int invoiceId = rs.getInt("id");
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
  public Invoice update(int id, Invoice updateInvoice) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(
              " update invoice i "
                  + "set date = ?, invoice_number = ? "
                  + "where i.id = " + id, new String[] {"seller", "buyer"});
          ps.setDate(1, Date.valueOf(updateInvoice.getDate()));
          ps.setString(2, updateInvoice.getNumber());
          return ps;
        }, keyHolder);
    Map<String, Object> companyKeys = keyHolder.getKeys();
    updateCompanies(companyKeys.get("seller"), updateInvoice.getSeller());
    updateCompanies(companyKeys.get("buyer"), updateInvoice.getBuyer());
    deleteEntries(id);
    addEntries(id, updateInvoice.getListOfInvoiceEntry());
    return null;
  }

  public void addEntries(int invoiceId, List<InvoiceEntry> list) {
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
        ps.setInt(5, vatToId
            .entrySet()
            .stream()
            .filter(vatEntry -> vatEntry.getValue() == invoiceEntry.getVatRate())
            .collect(Collectors.toList()).get(0).getKey());
        ps.setLong(6, insertCarReturnId(invoiceEntry, keyHolder));
        return ps;
      }, keyHolder);
      int invoiceEntryId = keyHolder.getKey().intValue();

      jdbcTemplate.update(coonection -> {
        PreparedStatement ps = coonection.prepareStatement(
            "insert into invoice_invoice_entry  (invoice_id,invoice_entry_id) values(?,?) ");
        ps.setInt(1, invoiceId);
        ps.setInt(2, invoiceEntryId);
        return ps;
      });
    });
  }

  public void deleteEntries(int invoiceId) {
    jdbcTemplate.update(connection -> {

      PreparedStatement ps = connection.prepareStatement(
          "delete from invoice_invoice_entry where invoice_id = ?");
      ps.setInt(1, invoiceId);
      return ps;
    });
  }

  public Invoice deleteInvoice(int id) {

    Invoice deletedInvoice = getById(id);
    jdbcTemplate.update("delete from invoice i where i.id =  " + id);
    return deletedInvoice;
  }

  public void updateCompanies(Object companyKey, Company company) {

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "update companies c "
              + "set tax_identification = ?, name = ?, address = ?, healthinsurancebasevalue = ?, "
              + "pensioninsurance = ?, amountofhealthinsurance = ?, amountofhealthinsurancetoreducetax = ? "
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
  public Invoice delete(int id) {

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
