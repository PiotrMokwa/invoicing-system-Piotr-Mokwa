package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Tax;

@Slf4j
@Data
@Service
public class TaxCalculatorService {

  private Database<Invoice> dataBase;
  private JsonService jsonService;
  private Company company;
  private BigDecimal incomeTaxPercent;

  public TaxCalculatorService(Database<Invoice> dataBase) {

    this.dataBase = dataBase;
    this.jsonService = new JsonService();
    this.incomeTaxPercent = BigDecimal.valueOf(0.19);
  }

  Predicate<Invoice> isBayer() {
    log.info("isBayer");
    return (Invoice invoice) ->
        Objects.equals(invoice.getBuyer().getTaxIdentification(), company.getTaxIdentification());
  }

  Predicate<Invoice> isSeller() {
    log.info("isSeller");
    return (Invoice invoice) ->
        Objects.equals(invoice.getSeller().getTaxIdentification(), company.getTaxIdentification());
  }

  Function<InvoiceEntry, BigDecimal> getVatIncludingCarForPersonalUse() {

    return value -> value.getExpansForCar().isPrivateUse()
        ? value.getVatValue()
        .divide(BigDecimal.valueOf(2), RoundingMode.HALF_DOWN)
        : value.getVatValue();
  }

  Function<InvoiceEntry, BigDecimal> getCostsIncludingCarForPersonalUse() {

    return value -> value.getExpansForCar().isPrivateUse()
        ? value.getPrice()
        .add(
            value.getVatValue().divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP))
        .setScale(2, RoundingMode.HALF_UP)
        : value.getPrice();
  }

  public BigDecimal incomingVat() {
    log.info("incomingVat");
    return visit(isSeller(), getVatIncludingCarForPersonalUse())
        .setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal outgoingVat() {
    log.info("outgoingVat");
    return visit(isBayer(), getVatIncludingCarForPersonalUse())
        .setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal income() {
    log.info("income");
    return visit(isSeller(), InvoiceEntry::getPrice);
  }

  public BigDecimal costs() {
    log.info("costs");
    return visit(isBayer(), getCostsIncludingCarForPersonalUse());
  }

  public BigDecimal earnings() {
    log.info("earnings");
    return income().subtract(costs());
  }

  public BigDecimal vatPayment() {
    log.info("vatPayment");
    return incomingVat().subtract(outgoingVat())
        .setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal taxCalculationBase() {
    return income()
        .subtract(costs())
        .subtract(company.getPensionInsurance());
  }

  public BigDecimal roundedTaxCalculationBase() {
    return taxCalculationBase().setScale(0, RoundingMode.UP).setScale(2);

  }

  public BigDecimal incomeTax() {
    return roundedTaxCalculationBase().multiply(getIncomeTaxPercent()).setScale(2, RoundingMode.UP);
  }

  public BigDecimal finalIncomeTaxValue() {

    return incomeTax().subtract(company.getAmountOfHealthInsuranceToReduceTax())
        .setScale(0, RoundingMode.HALF_DOWN).setScale(2);
  }

  public Tax taxVat() {

    return Tax.builder()
        .incomingVat(incomingVat())
        .outgoingVat(outgoingVat())
        .income(income())
        .costs(costs())
        .earnings(earnings())
        .vatToPay(vatPayment())
        .roundedTaxCalculationBase(roundedTaxCalculationBase())
        .build();
  }

  public TaxValues getTaxValues() {

    return TaxValues.builder()
        .income(income())
        .costs(costs())
        .incomeSubtractCosts(earnings())
        .pensionInsurance(company.getPensionInsurance())
        .taxCalculationBase(taxCalculationBase())
        .taxCalculationBaseRound(roundedTaxCalculationBase())
        .incomeTax(incomeTax())
        .healthInsuranceValue(company.getAmountOfHealthInsurance())
        .healthInsuranceValueForTax(company.getAmountOfHealthInsuranceToReduceTax())
        .finalIncomeTaxValue(finalIncomeTaxValue())
        .build();
  }

  public String getTaxInJson(Company company) {
    setCompany(company);
    String tax = jsonService.convertToJson(taxVat());
    return tax.substring(0, tax.length() - 2);
  }

  public String getTaxValuesInJson(Company company) {
    setCompany(company);
    String tax = jsonService.convertToJson(getTaxValues());
    return tax.substring(0, tax.length() - 2);
  }

  public BigDecimal visit(Predicate<Invoice> rules, Function<InvoiceEntry, BigDecimal> entry) {

    return dataBase.getAll()
        .stream()
        .filter(rules)
        .map(value -> value.getListOfInvoiceEntry()
            .stream()
            .map(entry)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
        )
        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.UP);
  }
}
