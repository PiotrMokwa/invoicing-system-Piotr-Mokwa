package pl.futurecollars.invoicing.model;

import lombok.Data;

@Data
public class Company {

  private String id;
  private int taxIdentyfication;
  private String address;
}
