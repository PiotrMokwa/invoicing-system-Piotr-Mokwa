package pl.futurecollars.invoicing.controller;

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
import pl.futurecollars.invoicing.service.InvoiceService;

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
    return Optional.ofNullable(invoiceService.save(invoice))
        .map(value -> ResponseEntity.status(HttpStatus.CREATED).body("Invoice nr. " + value + " was added"))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice wasn't added"));
  }

  @GetMapping(value = "GET Invoices", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> getInvoices() {
    return Optional.ofNullable(invoiceService.getAll())
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED).body(invoiceService.getAll()))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @GetMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> getInvoice(@PathVariable int id) {
    return Optional.ofNullable(invoiceService.getById(id))
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED).body(invoiceService.getById(id)))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @PutMapping(value = "/update/{id}", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> updateInvoice(@PathVariable int id, @RequestBody Invoice newInvocie) {
    return Optional.ofNullable(invoiceService.update(id, newInvocie))
        .map(value -> ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.update(id, newInvocie)))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @DeleteMapping(value = "/delete/{id}", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> delete(@PathVariable int id) {
    return Optional.ofNullable(invoiceService.delete(id))
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
}
