package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import spock.lang.Subject
import spock.lang.Title

@Title("testing InMemoryDataBase")
class InMemoryDatabaseTest extends TestHelpers {

    @Subject
    InMemoryDatabase inMemoryDatabase = createInMemoryBase()

    static createInMemoryBase() {
        Map<Integer, Invoice> invoices = new HashMap<>()
        InMemoryDatabase inMemoryDatabase = new InMemoryDatabase(1, invoices)
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        inMemoryDatabase.save(invoice1)
        inMemoryDatabase.save(invoice2)
        return inMemoryDatabase
    }

    def " test save invoice Positive"() {
        given:
        Invoice thirdInvoice = createFirstInvoice(
                createFirstCompany(), createSecondCompany())
        when:
        def result = inMemoryDatabase.save(thirdInvoice)
        then:
        result == thirdInvoice.getId()
    }


    def " get by ID"() {
        given:
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        invoice1.id = 1
        when:
        Invoice result = inMemoryDatabase.getById(1)
        then:
        result == invoice1

    }

    def " get all "() {
        given:
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice1.id = 1
        invoice2.id = 2
        List<Invoice> listOfInvoices = new ArrayList<>()
        listOfInvoices[0] = invoice1
        listOfInvoices[1] = invoice2
        when:
        List<Invoice> result = inMemoryDatabase.getAll()
        then:
        result == listOfInvoices
    }

    def " update True"() {
        given:
        Invoice invoice4 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice4.buyer.id = "Updated Company"
        invoice4.id = 1
        when:
        inMemoryDatabase.update(1, invoice4)
        def result = inMemoryDatabase.getById(1)
        then:
        invoice4 == result
    }

    def " update False"() {
        given:
        Invoice invoice4 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice4.buyer.id = "Updated Company"
        invoice4.id = 0
        when:
        Invoice result = inMemoryDatabase.update(5, invoice4)
        then:
        result == null
    }

    def "visit"() {
        when:
        BigDecimal sum = inMemoryDatabase.visit(Bayer("444-444-44-44"), getVatValue())
        then:
        sum == 16580.93

    }

    def "Bayer"(String companyIdNumber) {
        return (Invoice invoice) -> invoice.getBuyer()
                .getTaxIdentification() == (companyIdNumber)

    }

    def "getVatValue"() {
        return (InvoiceEntry entry) -> entry.vatValue
    }


    def " delete "() {
        when:
        boolean wasDeleted = inMemoryDatabase.delete(1)
        then:
        wasDeleted
    }
}
