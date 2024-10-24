package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.Generated;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.CompaniesService;

@Generated
@RestController
@RequestMapping(value = "companies", produces = {"application/json;charset=UTF-8"})
@Api(tags = {"companies controller"})
public class CompaniesController implements CompaniesControllerApi {

  CompaniesService companiesService;

  @Autowired
  public CompaniesController(CompaniesService companiesService) {
    this.companiesService = companiesService;

  }

  public ResponseEntity<?> addCompany(@RequestBody Company company) {
    return Optional.ofNullable(companiesService.save(company))
        .map(value -> ResponseEntity.status(HttpStatus.CREATED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  public ResponseEntity<?> getCompanies() {
    return Optional.ofNullable(companiesService.getAll())
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  public ResponseEntity<?> getCompany(
      @PathVariable("id")
      @Parameter(name = "id", description = "id", example = "1")
      Long id) {
    return Optional.ofNullable(companiesService.getById(id))
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  public ResponseEntity<?> updateCompany(
      @PathVariable("id")
      @Parameter(name = "id", description = "id", example = "1")
      Long idNumber,
      @RequestBody Company newCompany) {
    return Optional.ofNullable(companiesService.update(idNumber, newCompany))
        .map(value -> ResponseEntity.status(HttpStatus.CREATED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  public ResponseEntity<?> deleteCompany(
      @PathVariable("id")
      @Parameter(name = "id", description = "id", example = "1")
      Long idNumber) {
    return Optional.ofNullable(companiesService.delete(idNumber))
        .map(value -> ResponseEntity.status(HttpStatus.ACCEPTED).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
}
