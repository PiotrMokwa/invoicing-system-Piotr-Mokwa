package pl.futurecollars.invoicing;

import org.springframework.boot.SpringApplication;
import pl.futurecollars.invoicing.service.InvoiceController;

public class App {

  public static void main(String[] args) {
    SpringApplication.run(InvoiceController.class, args);
  }
}


