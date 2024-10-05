package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Optional;
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
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@Generated
@RestController
@RequestMapping("invoices")
@Api(tags = {"invoices controller"})
public class InvoiceController {

  InvoiceService invoiceService;

  @Autowired
  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @PostMapping("add")
  public ResponseEntity<?> addInvoice(@RequestBody Invoice invoice) {
    return Optional.ofNullable(invoiceService.save(invoice))
        .map(value -> ResponseEntity.status(HttpStatus.CREATED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @GetMapping(value = "GET Invoices", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> getInvoices() {
    return Optional.ofNullable(invoiceService.getAll())
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @GetMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> getInvoice(@PathVariable("id") @Parameter(name = "id", description = "id", example = "1") Long id) {
    return Optional.ofNullable(invoiceService.getById(id))
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @PutMapping(value = "/update/{id}", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> updateInvoice(
      @PathVariable("id") @Parameter(name = "id", description = "id", example = "1") Long id, @RequestBody Invoice newInvocie) {
    return Optional.ofNullable(invoiceService.update(id, newInvocie))
        .map(value -> ResponseEntity.status(HttpStatus.CREATED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @DeleteMapping(value = "/delete/{id}", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> delete(@PathVariable("id") @Parameter(name = "id", description = "id", example = "1") Long id) {
    return Optional.ofNullable(invoiceService.delete(id))
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
}
