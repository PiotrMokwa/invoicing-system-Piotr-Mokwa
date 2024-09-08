package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface TaxCalculatorControllerApi {

  @ApiOperation(value = "getTaxData")
  ResponseEntity<?> tax(@PathVariable String taxIdentificationNumber);
}
