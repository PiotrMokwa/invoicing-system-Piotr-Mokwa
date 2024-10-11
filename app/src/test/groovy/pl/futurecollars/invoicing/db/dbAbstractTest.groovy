package pl.futurecollars.invoicing.db

import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import spock.lang.Specification;

import static pl.futurecollars.invoicing.TestHelpers.*;

abstract class dbAbstractTest extends Specification {

    abstract Database getDataBaseInstance();

    Database database
    def setup() {
        database = getDataBaseInstance()
        System.out.println("setup before delete" + database.getAll())
        database.getAll().forEach(invoice -> database.delete(invoice.getId()))
        System.out.println("setup after delete" + database.getAll())
        "listOfInvoicesSavedToBase"()
        System.out.println("setup after add" + database.getAll())


    }

    def "setIdToComapnies"(List<Invoice> invoicesList) {

        invoicesList[0].getBuyer().setId(1)
        invoicesList[0].getSeller().setId(2)
        invoicesList[1].getBuyer().setId(3)
        invoicesList[1].getSeller().setId(4)
        return invoicesList
    }

    def "listOfInvoicesSavedToBase"() {
        def list = listOfInvoiceToTest()
        list = "setIdToComapnies"(list)
        database.save(list[0])
        database.save(list[1])
        System.out.println(" cala baza" + database.getAll())
        database.getAll().forEach(invoice->"setIdNull"(invoice))
        list.forEach(invoice->"setIdNull"(invoice))
        return list
    }

    def "setIdNull"(Invoice invoice){
        invoice.id = null
        invoice.getBuyer().id = null
        invoice.getSeller().id = null
        invoice.getListOfInvoiceEntry().get(0).setId(null)
        invoice.getListOfInvoiceEntry().get(1).setId(null)
        invoice.getListOfInvoiceEntry().get(0).getExpansForCar().setId(null)
        invoice.getListOfInvoiceEntry().get(1).getExpansForCar().setId(null)
        return invoice
    }

    def " test save third invoice"() {
        given:

        Invoice thirdInvoice = createFirstInvoice()
        def lastInviceId = database.save(thirdInvoice)
        when:
        def result = database.save(thirdInvoice)

        then:
        result == lastInviceId +1


    }

    def " get by ID"() {
        given:
        def invoiceFromBaseId = database.getAll().get(0).getId()
        def invoice1 = createFirstInvoice()
        "setIdNull"(invoice1)
        when:
        def result = database.getById(invoiceFromBaseId)
        "setIdNull"(result)
        then:
        result.toString() == invoice1.toString()

    }

    def " get all "() {
        given:
       
        when:
        List<Invoice> result = database.getAll()
        result.forEach(invoice->"setIdNull"(invoice))

        then:
        result.toString() == "listOfInvoicesSavedToBase"().toString()

    }

    def " update True"() {
        given:

        def newInvoice = createFirstInvoice()
        newInvoice.buyer.name = "Updated Company"
        "setIdNull"(newInvoice)
        def invoiceId =database.getAll().get(0).getId()

        System.out.println("get all" + database.getAll())
        System.out.println("id" + invoiceId)
        System.out.println("new invoice" + newInvoice)
        when:
        database.update(invoiceId, newInvoice)
        def result = database.getById(invoiceId)
        "setIdNull"(newInvoice)
        "setIdNull"(result)
        then:

        newInvoice.toString() == result.toString()

    }

    def "visit"() {
        given:
        when:
        BigDecimal sum = database.visit(Bayer("444-444-44-44"), getVatValue())
        then:
        sum == 906.36


    }

    def "Bayer"(String companyIdNumber) {
        return (Invoice invoice) -> invoice.getBuyer()
                .getTaxIdentification() == (companyIdNumber)

    }

    def "getVatValue"() {
        return (InvoiceEntry entry) -> entry.vatValue
    }


    def " delete "() {
given:
def invoiceId =database.getAll().get(0).getId()
        when:
        database.delete(invoiceId)
        def emptyInvoice = database.getById(invoiceId)
        then:
        emptyInvoice == null


    }
}
