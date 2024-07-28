package pl.futurecollars.invoicing.model;
public enum Vat {
  vat_23(23),
  vat_8(8),
  vat_5(5),
  vat_0(0);
  private final int vatValue;
  Vat(int number){
this.vatValue = number;
  };

}
