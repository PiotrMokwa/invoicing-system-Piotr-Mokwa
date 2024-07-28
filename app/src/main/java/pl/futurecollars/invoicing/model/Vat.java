package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Vat {
  vat_23(23),
  vat_8(8),
  vat_5(5),
  vat_0(0);
  private final BigDecimal vatValue;

  Vat(int number) {
    this.vatValue = BigDecimal.valueOf(number);
  }
}
