package pl.futurecollars.invoicing.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class Invoice {

  private int id;
  private LocalDate date;
  private Company buyer;
  private Company seller;
  private List<InvoiceEntry> listOfInvoice;

  public Invoice() {
  }

  public Invoice(LocalDate date, Company buyer, Company seller, List<InvoiceEntry> listOfInvoice) {
    this.date = date;
    this.buyer = buyer;
    this.seller = seller;
    this.listOfInvoice = listOfInvoice;
  }
}
