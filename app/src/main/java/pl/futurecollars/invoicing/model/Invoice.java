package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class Invoice {

  private int id;
  @ApiModelProperty(value = "Data", required = true, example = "2020-01-01")
  private LocalDate date;
  private Company buyer;
  private Company seller;
  private List<InvoiceEntry> listOfInvoiceEntry;

  public Invoice() {
  }

  public Invoice(LocalDate date, Company buyer, Company seller, List<InvoiceEntry> listOfInvoice) {

    this.date = date;
    this.buyer = buyer;
    this.seller = seller;
    this.listOfInvoiceEntry = listOfInvoice;
  }
}
