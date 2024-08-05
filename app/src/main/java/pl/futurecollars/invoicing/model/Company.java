package pl.futurecollars.invoicing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Company {

  private String id;
  private int taxIdentyfication;
  private String address;

  public Company() {
  }
}
