package pl.futurecollars.invoicing.controller.company;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.model.Company;

@RequestMapping(value = "companies", produces = {"application/json;charset=UTF-8"})
@Api(tags = {"companies controller"})
public interface CompaniesControllerApi {

  @ApiOperation(value = "Add new company")
  @PostMapping
  long addCompany(@RequestBody Company company);

  @ApiOperation(value = "Get all companies")
  @GetMapping(produces = {"application/json;charset=UTF-8"})
  ResponseEntity<?> getCompanies();

  @ApiOperation(value = "Get single company with selected id")
  @GetMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
  ResponseEntity<?> getCompany(@PathVariable("id")
                               @Parameter(name = "id", description = "id", example = "1")
                               Long idNumber);

  @ApiOperation(value = "Update company with selected id")
  @PutMapping(value = "/update/{id}", produces = {"application/json;charset=UTF-8"})
  ResponseEntity<?> updateCompany(@PathVariable("id")
                                  @Parameter(name = "id", description = "id", example = "1")
                                  Long idNumber,
                                  @RequestBody Company newCompany);

  @ApiOperation(value = "Delete company with selected id")
  @DeleteMapping(value = "/delete/{id}", produces = {"application/json;charset=UTF-8"})
  ResponseEntity<?> deleteCompany(@PathVariable("id")
                                  @Parameter(name = "id", description = "id", example = "1")
                                  Long idNumber);
}
