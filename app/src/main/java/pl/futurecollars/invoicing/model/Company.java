package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

@Builder
@Service
@AllArgsConstructor
@Data
public class Company {

  @ApiModelProperty(value = "Company number", required = true, example = "1")
  private String id;
  @ApiModelProperty(value = "Tax identyfication number", required = true, example = "444-444-44-44")
  private String taxIdentification;
  @ApiModelProperty(value = "Company name", required = true, example = "ORLEN")
  private String name;
  @ApiModelProperty(value = "Adress", required = true, example = "Berlin, street Leopolda 7")
  private String address;
  @ApiModelProperty(value = "healthInsurance", required = true, example = "3554.89")
  private BigDecimal healthInsuranceBaseValue;
  @ApiModelProperty(value = "healthInsurance", required = true, example = "514.57")
  private BigDecimal pensionInsurance;
  private BigDecimal amountOfHealthInsurance;
  private BigDecimal amountOfHealthInsuranceToReduceTax;

  public Company() {
  }
}
