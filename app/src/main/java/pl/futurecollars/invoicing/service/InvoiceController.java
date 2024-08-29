package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.model.Invoice;

@Generated
@Slf4j
@RestController
@RequestMapping("invoices")
public class InvoiceController {

  InvoiceService invoiceService;

  @Autowired
  public InvoiceController(InvoiceService invoiceService, IdService idService) {
    this.invoiceService = invoiceService;
  }

  @PostMapping("add")
  public ResponseEntity<?> addInvoice(@RequestBody Invoice invoice) {

    int invoiceId = invoiceService.save(invoice);
    Optional<Invoice> getAddedInvoice = invoiceService.getById(invoiceId);
    if (getAddedInvoice.isEmpty()) {
      getAddedInvoice = null;
    }
    Optional<Invoice> finalGetAddedInvoice = getAddedInvoice;
    String logMessage = getAddedInvoice.isEmpty() ? "Server aces point: not add" : "Server aces point: add";
    log.info(logMessage);
    return Optional.ofNullable(getAddedInvoice)
        .map(value1 -> ResponseEntity.status(HttpStatus.CREATED)
            .body(
                "Invoice nr. " + finalGetAddedInvoice.get().getId() + " was added")
        )
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice wasn't added"));
  }

  @GetMapping(value = "GET Invoices", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> getInvoices() {

    List<Invoice> list;
    list = invoiceService.getAll().isEmpty() ? null : invoiceService.getAll();
    String logMessage = "Server aces point GET Invoices :"
        + (list == null ? " null" : " not null");

    log.info(logMessage);

    return Optional.ofNullable(list)
        .map(
            value -> ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(list))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @GetMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> getInvoice(@PathVariable int id) {
    Optional<Invoice> invoice;
    invoice = invoiceService.getById(id).isEmpty() ? null : invoiceService.getById(id);
    String logMessage = "Server aces point: GET invoice:" + !(invoice == null);
    log.info(logMessage);
    return Optional.ofNullable(invoice)
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(invoice)
        )
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @PutMapping(value = "/update/{id}", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> updateInvoice(@PathVariable int id, @RequestBody Invoice newInvocie) {

    boolean isOldInvoiceEmpty = invoiceService.update(id, newInvocie).isEmpty();
    Optional<Invoice> newInvoiceFromBase = invoiceService.getById(id);
    Optional<Invoice> invoiceHolder = isOldInvoiceEmpty ? null : newInvoiceFromBase;

    String logMessage = "Server aces point: update invoice nr:"
        + id
        + " "
        + !isOldInvoiceEmpty;
    log.info(logMessage);
    return Optional.ofNullable(invoiceHolder)
        .map(value -> ResponseEntity.status(HttpStatus.CREATED)
            .body(newInvoiceFromBase)
        )
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> delete(@PathVariable int id) {
    Optional<Boolean> wasElementToDelete = Optional.of(invoiceService.delete(id));
    wasElementToDelete = (wasElementToDelete.get() == true) ? wasElementToDelete : null;
    String logMessage = "Server aces point: delete invoice nr: "
        + id
        + " "
        + !(wasElementToDelete == null);
    log.info(logMessage);
    return Optional.ofNullable(wasElementToDelete)
        .map(
            value -> ResponseEntity.status(HttpStatus.ACCEPTED).body("Invoice was deleted")
        ).orElseGet(
            () -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice was not deleted"));
  }
}
