package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@ToString
public enum Vat {

  vat_23(23),
  vat_8(8),
  vat_5(5),
  vat_0(0);
  private final BigDecimal vatValue;

  @Autowired
  Vat(int number) {
    this.vatValue = BigDecimal.valueOf(number).divide(BigDecimal.valueOf(100), 3, RoundingMode.UP);
  }
}
