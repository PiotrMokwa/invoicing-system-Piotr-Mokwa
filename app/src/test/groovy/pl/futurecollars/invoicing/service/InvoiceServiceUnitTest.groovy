package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification
import pl.futurecollars.invoicing.db.Database
import spock.lang.Title

@Title("Testing Invoice Service")
class InvoiceServiceUnitTest extends Specification {

    def "get data by ID"() {

        setup:
        def dataBase = Mock(Database)
        InvoiceService invoiceService = new InvoiceService(dataBase)

        when:
        Invoice invoice = new Invoice()
        Optional<Invoice> invoiceOptional = Optional.of(invoice)

        dataBase.getById(id) >> invoiceOptional
        def result = invoiceService.getById(id)

        then:
        result == invoiceOptional
        where: " input Data"
        id = 0

    }

    def "test update"() {

        setup: " moc dataBase"
        def dataBase = Mock(Database)
        InvoiceService invoiceService = new InvoiceService(dataBase)
        Invoice invoice = new Invoice()
        def i = 0
        dataBase.update(id, invoice) >> { Optional.of(i++)  }

        when: " method i-times executed"

        invoiceService.update(id, invoice)

        then:
        id == i

        where: " i-times"
        id = 1
    }

    def "test getAll"() {

        setup: " moc dataBase"
        def dataBase = Mock(Database)
        InvoiceService invoiceService = new InvoiceService(dataBase)

        List<Invoice> list = new ArrayList<>()
        dataBase.getAll() >> list

        when: " method result"

        List<Invoice> result = invoiceService.getAll()

        then: " compare"
        result == list
    }

    def "test delete"() {

        setup: " moc dataBase"
        def dataBase = Mock(Database)
        InvoiceService invoiceService = new InvoiceService(dataBase)
        dataBase.delete(0) >> true
        when: " delate invoice"
        boolean result = invoiceService.delete(0)
        then: " check if is not empty"
        result
    }

}
