package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@Data
public class Insurance {

  @ApiModelProperty(value = "healthInsurance", required = true, example = "1234.54")
  private BigDecimal healthInsuranceBaseValue;

  @ApiModelProperty(value = "healthInsurance", required = true, example = "1234.54")
  private BigDecimal pensionInsurance;

  private final BigDecimal amountOfHealthInsurance;

  private final BigDecimal amountOfHealthInsuranceToReduceTax;

  public Insurance(BigDecimal healthInsuranceBaseValue, BigDecimal pensionInsurance) {
    this.healthInsuranceBaseValue = healthInsuranceBaseValue;
    this.pensionInsurance = pensionInsurance;
    this.amountOfHealthInsurance = healthInsuranceBaseValue
        .multiply(BigDecimal.valueOf(0.09))
        .setScale(2, RoundingMode.DOWN);
    this.amountOfHealthInsuranceToReduceTax = healthInsuranceBaseValue
        .multiply(BigDecimal.valueOf(0.0775))
        .setScale(2, RoundingMode.DOWN);
  }
}
