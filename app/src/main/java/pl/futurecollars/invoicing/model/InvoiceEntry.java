package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data

public class InvoiceEntry {

  @ApiModelProperty(value = "Product/service description", required = true, example = " toner/cleaning windows")
  private String description;
  @ApiModelProperty(value = "Price value", required = true, example = " 20", dataType = "BigDecimal")
  private BigDecimal price;
  @ApiModelProperty(value = "Price value", required = true, example = " 20")
  private BigDecimal vatValue;
  @ApiModelProperty(value = "Vat tax rate", required = true)
  private Vat vatRate;


}
