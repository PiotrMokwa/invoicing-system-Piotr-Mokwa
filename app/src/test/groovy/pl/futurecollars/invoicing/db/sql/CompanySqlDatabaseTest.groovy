package pl.futurecollars.invoicing.db.sql

import org.flywaydb.core.Flyway
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Company
import spock.lang.Specification
import javax.sql.DataSource
import static pl.futurecollars.invoicing.TestHelpers.createFirstCompany
import static pl.futurecollars.invoicing.TestHelpers.listOfCompaniesToTest

class CompanySqlDatabaseTest extends Specification {

    def database = getDataBaseInstance()

    Database getDataBaseInstance() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource)

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration")
                .load()
        flyway.clean()
        flyway.migrate()

        def database = new CompanySqlDatabase(jdbcTemplate)
        return database
    }

    def "listOfCompaniesSavedToBase"() {
        def list = listOfCompaniesToTest()
        list[0].setId(null)
        list[1].setId(null)
        database.save(list[0])
        database.save(list[1])
        System.out.println(" cala baza" + database.getAll())

        return list


    }


    def setup() {

        System.out.println("setup before delete" + database.getAll())
        database.getAll().forEach(invoice -> database.delete(invoice.getId()))
        System.out.println("setup after delete" + database.getAll())
        "listOfCompaniesSavedToBase"()
        System.out.println("setup after add" + database.getAll())


    }

    def "Save"() {

        given: " test save third company"
        def company = createFirstCompany()
        System.out.println(" 1: " + database.getAll().size())
        def lastCompanyId = database.save(company)
        System.out.println(" 2: " + database.getAll().size())

        when:
        def result = database.save(company)
        System.out.println(" 3: " + database.getAll().size())

        then:
        result == lastCompanyId + 1
    }

    def "GetById"() {
        given:
        def companyFromBaseId = database.getAll().get(0).properties.get("id")
        System.out.println("properties " + database.getAll().get(0).properties.keySet())
        System.out.println("get all" + database.getAll())
        System.out.println("list of invoice" + "listOfCompaniesSavedToBase"())

        def company1 = createFirstCompany()

        when:
        def result = database.getById(companyFromBaseId)
        result.setId(null)

        then:
        result.toString() == company1.toString()
    }

    def "GetAll"() {

        when:
        List<Company> result = database.getAll()
        result.forEach(company -> company.setId(null))

        then:
        result.toString() == "listOfCompaniesSavedToBase"().toString()
    }

    def "Update"() {

        given:

        def company = createFirstCompany()
        company.name = "Updated Company"

        def companyId = database.getAll().get(0).getId()

        System.out.println("get all" + database.getAll())
        System.out.println("id" + companyId)
        System.out.println("new company" + company)

        when:
        database.update(companyId, company)
        def result = database.getById(companyId)
        result.setId(null)

        then:
        company.toString() == result.toString()
    }

    def "Delete"() {

        given:
        def companyId = database.getAll().get(0).getId()
        when:
        database.delete(companyId)
        def emptyInvoice = database.getById(companyId)
        then:
        emptyInvoice == null
    }
}
