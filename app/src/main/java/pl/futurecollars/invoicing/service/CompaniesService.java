package pl.futurecollars.invoicing.service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;

@Slf4j
@Builder
@AllArgsConstructor
@Service
public class CompaniesService {

  private final Database<Company> dataBase;

  public Company getById(Long id) {
    log.info("Companies Service getById");
    return dataBase.getById(id);
  }

  public List<Company> getAll() {
    log.info("Companies Service getAll");
    System.out.println(dataBase.getAll());
    return dataBase.getAll().isEmpty() ? null : dataBase.getAll();
  }

  public Company update(Long id, Company updateInvoice) {
    log.info("Companies Service update");
    return dataBase.update(id, updateInvoice);
  }

  public Company delete(Long id) {
    log.info("Companies Service delete");
    return dataBase.delete(id);
  }

  public Long save(Company company) {
    log.info("Companies Service save");
    return dataBase.save(company);
  }

}
