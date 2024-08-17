package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.Generated;
import pl.futurecollars.invoicing.InvoiceSetup;
import pl.futurecollars.invoicing.db.file.FileBasedDataBase;
import pl.futurecollars.invoicing.model.Invoice;

@Generated
@SpringBootApplication
@RestController
@RequestMapping("invoices")
public class InvoiceController {

  private final InvoiceSetup invoiceSetup = new InvoiceSetup("Base", "IdInvoice");
  //  private Map<Integer, Invoice> invoices = new HashMap<>();
  //  private InvoiceService invoiceService = new InvoiceService(new InMemoryDatabase(0, invoices));
  private InvoiceService invoiceService = new InvoiceService(new FileBasedDataBase(invoiceSetup));

  @PostMapping("add")
  public ResponseEntity<?> addInvoice(@RequestBody Invoice invoice) {

    invoiceService.save(invoice);
    Optional<Invoice> getAddedInvoice = invoiceService.getById(invoiceService.getAll().size() - 1);
    if (getAddedInvoice.isEmpty()) {
      getAddedInvoice = null;
    }

    Optional<Invoice> finalGetAddedInvoice = getAddedInvoice;

    return Optional.ofNullable(getAddedInvoice)
        .map(value1 -> ResponseEntity.status(HttpStatus.CREATED)
            .body(
                "Invoice nr. " + finalGetAddedInvoice.get().getId() + " was added")
        )
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice wasn't added"));
  }

  @GetMapping("GET Invoices")
  public ResponseEntity<List<Invoice>> getInvoices() {
    List<Invoice> list;

    list = invoiceService.getAll().isEmpty() ? null : invoiceService.getAll();
    System.out.println(list);
    return Optional.ofNullable(list)
        .map(
            value -> ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(list))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getInvoice(@PathVariable int id) {
    Optional<Invoice> invoice;
    invoice = invoiceService.getById(id).isEmpty() ? null : invoiceService.getById(id);

    return Optional.ofNullable(invoice)
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(invoice)
        )
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<?> updateInvoice(@PathVariable int id, @RequestBody Invoice newInvocie) {

    boolean isOldInvoiceEmpty = invoiceService.update(id, newInvocie).isEmpty();
    Optional<Invoice> newInvoiceFromBase = invoiceService.getById(id);

    Optional<Invoice> invoiceHolder = isOldInvoiceEmpty ? null : newInvoiceFromBase;

    return Optional.ofNullable(invoiceHolder)
        .map(value -> ResponseEntity.status(HttpStatus.CREATED)
            .body(invoiceHolder.get())
        )
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> delete(@PathVariable int id) {
    Optional<Boolean> wasElementToDelete = Optional.of(invoiceService.delete(id));
    wasElementToDelete = (wasElementToDelete.get() == true) ? wasElementToDelete : null;
    return Optional.ofNullable(wasElementToDelete)
        .map(
            value -> ResponseEntity.status(HttpStatus.ACCEPTED).body("Invoice was deleted")
        ).orElseGet(
            () -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice was not deleted"));

  }
}
