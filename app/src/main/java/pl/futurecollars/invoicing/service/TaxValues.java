package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaxValues {

  BigDecimal income;
  BigDecimal costs;
  BigDecimal incomeSubtractCosts;
  BigDecimal pensionInsurance;
  BigDecimal taxCalculationBase;
  BigDecimal taxCalculationBaseRound;
  BigDecimal incomeTax;
  BigDecimal healthInsuranceValue;
  BigDecimal healthInsuranceValueForTax;
  BigDecimal finalIncomeTaxValue;

}
