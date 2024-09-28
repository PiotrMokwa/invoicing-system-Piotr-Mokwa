package pl.futurecollars.invoicing.db

import pl.futurecollars.invoicing.model.Company;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import spock.lang.Specification;

import static pl.futurecollars.invoicing.TestHelpers.*;

abstract class dbAbstractTest extends Specification {


    Database database = getDataBaseInstance()

    abstract Database getDataBaseInstance();

    def setup() {
        " listOfInvoicesSavedToBase"()
    }

    def "setIdToComapnies"( List<Invoice> invoicesList){

        invoicesList[0].getBuyer().setId("1")
        invoicesList[0].getSeller().setId("2")
        invoicesList[1].getBuyer().setId("3")
        invoicesList[1].getSeller().setId("4")
        return invoicesList
    }

    def " listOfInvoicesSavedToBase"(){
        def list = listOfInvoiceToTest()
        list = "setIdToComapnies"(list)
        database.save(list[0])
        database.save(list[1])
        return list
    }

    def " test save third invoice"() {
        given:
        Invoice thirdInvoice = createFirstInvoice()
        thirdInvoice.id = 3
        when:
        def result = database.save(thirdInvoice)
        then:
        result == 3
    }


    def " get by ID"() {
        given:
        Invoice invoice1 = createFirstInvoice()
        invoice1.id = 1
        invoice1.getBuyer().id = "1"
        invoice1.getSeller().id = "2"

        when:
        Invoice result = database.getById(1)
        then:
        result == invoice1

    }

    def " get all "() {

        when:
        List<Invoice> result = database.getAll()
        then:
        result == " listOfInvoicesSavedToBase"()
    }

    def " update True"() {
        given:
        def newInvoice = createFirstInvoice()
        newInvoice.buyer.name = "Updated Company"
        newInvoice.id = 1
        newInvoice.getBuyer().id = "1"
        newInvoice.getSeller().id = "2"
        when:
        database.update(1, newInvoice)
        def result = database.getById(1)
        then:
        result == newInvoice
    }

    def "visit"() {

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

        when:
        database.delete(1)
        def deletedInvoice = database.getById(1)
        def emptyInvoice =database.getById(1)
        then:
        emptyInvoice == null
    }
}
