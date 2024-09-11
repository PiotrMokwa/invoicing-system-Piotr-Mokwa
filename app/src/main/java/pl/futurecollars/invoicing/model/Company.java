package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Data
public class Company {

  @ApiModelProperty(value = "Company number", required = true, example = "1")
  private String id;

  @ApiModelProperty(value = "Tax identyfication number", required = true, example = "444-444-44-44")
  private String taxIdentyfication;
  @ApiModelProperty(value = "Adress", required = true, example = "Berlin, street Leopolda 7")
  private String address;
  @ApiModelProperty(value = "insurance taxes", required = true)
  private Insurance insurance;

  public Company() {
  }
}
