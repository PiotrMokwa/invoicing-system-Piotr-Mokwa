package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import pl.futurecollars.invoicing.model.Company;

public interface TaxCalculatorControllerApi {

  @ApiOperation(value = "getTaxData")
  ResponseEntity<?> tax(@RequestBody Company company);
}
