package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.JsonService;
import pl.futurecollars.invoicing.service.TaxCalculatorService;

@Api(tags = {"tax"})
@RestController
@RequestMapping("taxCalculator")
public class TaxCalculatorController implements TaxCalculatorControllerApi {

  TaxCalculatorService taxCalculatorService;
  JsonService jsonService;

  TaxCalculatorController(TaxCalculatorService taxCalculatorService) {
    this.taxCalculatorService = taxCalculatorService;
    this.jsonService = new JsonService();
  }

  @Override
  @PostMapping(value = "/company", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<?> tax(
      @Parameter(name = "company", description = "company", example = "company object")
      @RequestBody Company company) {
    return Optional.ofNullable(taxCalculatorService.getTaxValuesInJson(company))
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

}
