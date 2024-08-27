package pl.futurecollars.invoicing.db.memory
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Subject
import spock.lang.Title

@Title("testing InMemoryDataBase")
class InMemoryDatabaseTest extends TestHelpers {

    @Subject
    InMemoryDatabase inMemoryDatabase = createInMemoryBase()

    static createInMemoryBase() {
        Map<Integer, Invoice> invoices = new HashMap<>()
        InMemoryDatabase inMemoryDatabase = new InMemoryDatabase(0, invoices)
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        inMemoryDatabase.save(invoice1)
        inMemoryDatabase.save(invoice2)
        return inMemoryDatabase
    }

    def " test save invoice"() {
        given:
        System.out.println(inMemoryDatabase.getAll().size())
        Invoice thirdInvoice = createFirstInvoice(
                createFirstCompany(), createSecondCompany())
        when:
        boolean result = inMemoryDatabase.save(thirdInvoice)
        then:
        result
                &
                inMemoryDatabase.getById(2) == Optional.of(thirdInvoice)

    }

    def " get by ID"() {
        given:
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        invoice1.id = 0
        when:
        Optional<Invoice> result = inMemoryDatabase.getById(0)
        then:
        result == Optional.of(invoice1)

    }

    def " get all "() {
        given:
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice1.id = 0
        invoice2.id = 1
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
        invoice4.id = 0
        when:
        inMemoryDatabase.update(0, invoice4)
        then:
        Optional.of(invoice4) == inMemoryDatabase.getById(0)
    }

    def " update False"() {
        given:
        Invoice invoice4 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice4.buyer.id = "Updated Company"
        invoice4.id = 0
        when:
        Optional<Invoice> result =inMemoryDatabase.update(5, invoice4)
        then:
         result.isEmpty()
    }

    def " delete "() {
        when:
        boolean wasDeleted = inMemoryDatabase.delete(0)
        boolean isNotInDataBase =  inMemoryDatabase.getById(0).isEmpty()
        then:
        wasDeleted & isNotInDataBase
    }
}
