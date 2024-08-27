package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data

public class InvoiceEntry {

  private String description;
  private BigDecimal price;
  private BigDecimal vatValue;
  private Vat vatRate;


}
