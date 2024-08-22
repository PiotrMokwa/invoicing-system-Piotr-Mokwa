package pl.futurecollars.invoicing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Data
public class Company {

  private String id;
  private int taxIdentyfication;
  private String address;

  public Company() {
  }
}
