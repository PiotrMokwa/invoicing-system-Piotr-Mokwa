package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Tax;

@Slf4j
@Data
@Service
public class TaxCalculatorService {

  private Database dataBase;
  private JsonService jsonService;

  public TaxCalculatorService(Database dataBase) {
    this.dataBase = dataBase;
    this.jsonService = new JsonService();

  }

  Predicate<Invoice> isBayer(String companyIdNumber) {
    log.info("isBayer");
    return (Invoice invoice) ->
        Objects.equals(invoice.getBuyer().getTaxIdentyfication(), companyIdNumber);
  }

  Predicate<Invoice> isSeller(String companyIdNumber) {
    log.info("isSeller");
    return (Invoice invoice) ->
        Objects.equals(invoice.getSeller().getTaxIdentyfication(), companyIdNumber);
  }

  public BigDecimal incomingVat(String companyIdNumber) {
    log.info("incomingVat");
    return dataBase.visit(isSeller(companyIdNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal outgoingVat(String companyIdNumber) {
    log.info("outgoingVat");
    return dataBase.visit(isBayer(companyIdNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal income(String companyIdNumber) {
    log.info("income");
    return dataBase.visit(isSeller(companyIdNumber), InvoiceEntry::getPrice);
  }

  public BigDecimal costs(String companyIdNumber) {
    log.info("costs");
    return dataBase.visit(isBayer(companyIdNumber), InvoiceEntry::getPrice);
  }

  public BigDecimal earnings(String companyIdNumber) {
    log.info("earnings");
    return income(companyIdNumber).subtract(costs(companyIdNumber));
  }

  public BigDecimal vatPayment(String companyIdNumber) {
    log.info("vatPayment");
    return incomingVat(companyIdNumber).subtract(outgoingVat(companyIdNumber));
  }

  public Tax tax(String companyIdNumber) {
    Tax tax = new Tax();
    tax.setIncomingVat(incomingVat(companyIdNumber));
    tax.setOutgoingVat(outgoingVat(companyIdNumber));
    tax.setIncome(income(companyIdNumber));
    tax.setCosts(costs(companyIdNumber));
    tax.setEarnings(earnings(companyIdNumber));
    tax.setVatToPay(vatPayment(companyIdNumber));
    return tax;
  }

  public String getTaxInJson(String companyIdNumber) {
    String tax = jsonService.convertToJson(tax(companyIdNumber));
    return tax.substring(0, tax.length() - 2);

  }
}
