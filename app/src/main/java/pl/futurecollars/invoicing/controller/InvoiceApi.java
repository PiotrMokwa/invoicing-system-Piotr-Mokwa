package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.futurecollars.invoicing.model.Invoice;

public interface InvoiceApi {
  @ApiOperation(value = "Add new invoice")
  @PostMapping("add")
  ResponseEntity<?> addInvoice(@RequestBody Invoice invoice);

  @ApiOperation(value = "Get all invoices")
  @GetMapping(value = "GET Invoices", produces = {"application/json;charset=UTF-8"})
  ResponseEntity<?> getInvoices();

  @ApiOperation(value = "Get single invoice with selected id")
  @GetMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
  ResponseEntity<?> getInvoice(@PathVariable int id);

  @ApiOperation(value = "Update invoice with selected id")
  @PutMapping(value = "/update/{id}", produces = {"application/json;charset=UTF-8"})
  ResponseEntity<?> updateInvoice(@PathVariable int id, @RequestBody Invoice newInvocie);

  @ApiOperation(value = "Delete invoice with selected id")
  @DeleteMapping("/delete/{id}")
  ResponseEntity<?> delete(@PathVariable int id);
}
