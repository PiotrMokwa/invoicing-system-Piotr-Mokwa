package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.service.TaxCalculatorService;

@Api(tags = {"tax"})
@RestController
@RequestMapping("taxCalculator")
public class TaxCalculatorController implements TaxCalculatorControllerApi {

  TaxCalculatorService taxCalculatorService;

  TaxCalculatorController(TaxCalculatorService taxCalculatorService) {
    this.taxCalculatorService = taxCalculatorService;
  }

  @Override
  @GetMapping(value = "/{taxIdentificationNumber}", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> tax(
      @PathVariable("taxIdentificationNumber")
      @Parameter(name = "taxIdentificationNumber", description = "taxIdentificationNumber", example = "444-444-44-44")
      String taxIdentificationNumber
  ) {
    return Optional.ofNullable(taxCalculatorService.getTaxInJson(taxIdentificationNumber))
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

}
