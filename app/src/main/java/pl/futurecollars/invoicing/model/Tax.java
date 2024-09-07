package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class Tax {

  BigDecimal incomingVat;
  BigDecimal outgoingVat;
  BigDecimal income;
  BigDecimal costs;
  BigDecimal earnings;
  BigDecimal vatToPay;

}
