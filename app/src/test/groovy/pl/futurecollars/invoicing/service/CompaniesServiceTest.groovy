package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Company
import spock.lang.Title

@Title("Testing Company Service")
class CompaniesServiceTest extends TestHelpers {

    def "GetById"() {
        setup:
        def dataBase = Mock(Database<Company>)
        CompaniesService companiesService = new CompaniesService(dataBase)
        when:
        Company company = new Company()
        dataBase.getById(id) >> company
        def result = companiesService.getById(id)
        then:
        result == company
        where: " input Data"
        id = 0


    }

    def "GetAll"() {
        setup:
        def dataBase = Mock(Database<Company>)
        CompaniesService companiesService = new CompaniesService(dataBase)

        List<Company> list = listOfInvoiceToTest()
        dataBase.getAll() >> list

        when: "method result"

        List<Company> result = companiesService.getAll()

        then: " compare"
        result == list
    }

    def "Update"() {
        setup:
        def dataBase = Mock(Database<Company>)
        CompaniesService companiesService = new CompaniesService(dataBase)
        Company company = new Company()
        def id = 0
        dataBase.update(id, company) >> company

        when: "method result"

        def result = companiesService.update(id, company)

        then:
        result == company
    }

    def "Delete"() {
        setup:
        def dataBase = Mock(Database<Company>)
        CompaniesService companiesService = new CompaniesService(dataBase)

        def company = createFirstCompany()
        dataBase.delete(1) >> company
        when: " delete company"
        def result = companiesService.delete(1)
        then: " check if is not empty"
        result == company
    }

    def "Save"() {

        setup:
        def dataBase = Mock(Database<Company>)
        CompaniesService companiesService = new CompaniesService(dataBase)
        def company = createFirstCompany()
        dataBase.save(company) >> 1

        when: " save company"
        def result = companiesService.save(company)

        then: " check if is not empty"
        result == 1
    }

}
