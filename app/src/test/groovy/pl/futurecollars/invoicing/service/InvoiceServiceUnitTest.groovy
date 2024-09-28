package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.db.Database
import spock.lang.Title

@Title("Testing Invoice Service")
class InvoiceServiceUnitTest extends TestHelpers {

    def "get data by ID"() {

        setup:
        def dataBase = Mock(Database)
        InvoiceService invoiceService = new InvoiceService(dataBase)
        when:
        Invoice invoice = new Invoice()
        dataBase.getById(id) >> invoice
        def result = invoiceService.getById(id)
        then:
        result == invoice
        where: " input Data"
        id = 0
    }

    def "test update"() {

        setup: " moc dataBase"
        def dataBase = Mock(Database)
        InvoiceService invoiceService = new InvoiceService(dataBase)
        Invoice invoice = new Invoice()
        def id = 0
        dataBase.update(id, invoice) >> invoice

        when: "method result"

       def result =  invoiceService.update(id, invoice)

        then:
        result == invoice

    }

    def "test getAll null"() {

        setup: " moc dataBase"
        def dataBase = Mock(Database)
        InvoiceService invoiceService = new InvoiceService(dataBase)

        List<Invoice> list = new ArrayList<>()
        dataBase.getAll() >> list

        when: " method result"

        List<Invoice> result = invoiceService.getAll()

        then: " compare"
        result == null
    }

    def "test getAll not empty"() {

        setup: " moc dataBase"
        def dataBase = Mock(Database)
        InvoiceService invoiceService = new InvoiceService(dataBase)

        List<Invoice> list = listOfInvoiceToTest()
        dataBase.getAll() >> list

        when: "method result"

        List<Invoice> result = invoiceService.getAll()

        then: " compare"
        result == list
    }

    def "test delete"() {

        setup: " moc dataBase"
        def dataBase = Mock(Database)
        InvoiceService invoiceService = new InvoiceService(dataBase)
        def invoice = createFirstInvoice()
        dataBase.delete(1) >> invoice
        when: " delate invoice"
        def result = invoiceService.delete(1)
        then: " check if is not empty"
        result == invoice
    }

}
