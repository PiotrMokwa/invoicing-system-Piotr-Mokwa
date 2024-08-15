package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.InvoiceSetup
import pl.futurecollars.invoicing.db.CommonDataBaseTest
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Subject
import java.nio.file.Files
import java.nio.file.Path

class FileBasedDataBaseTest extends CommonDataBaseTest {

    @Subject
    FileBasedDataBase fileBasedDataBase = createFileBase()

    void cleanup(){
        "delete files"()
    }

    def "test save to FileBase ()"() {

        given: " create third invoice in FileBase"
//        FileBasedDataBase fileBasedDataBase = createFileBase()
        Invoice thirdInvoice = createFirstInvoice(
                createFirstCompany(), createSecondCompany()
        )

        when: "save"

        fileBasedDataBase.save(thirdInvoice)
        Invoice thirdInvoiceFromBase = fileBasedDataBase
                .getById(2)
                .get()
        then: " compare added invoice with invoice from base"
        thirdInvoiceFromBase == thirdInvoice
    }

    def "test get by ID from FileBase"() {

        given: " create the same invoice as in the base"
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice2.setId(1)
        when: "get By Id"
        Optional secondInvoiceFromBase = fileBasedDataBase
                .getById(1)
        then: " compare created invice with invoice geted from base"
        secondInvoiceFromBase.get() == invoice2
    }

    def "test get all from FileBase true"() {
        given:
        List<Invoice> listOfTestedInvoice =listOfInvoiceToTest()

        when: "get all"
        List<Invoice> listOfInvoice = fileBasedDataBase.getAll()
        then:
        listOfTestedInvoice == fileBasedDataBase.getAll()

    }

    def "test get all from FileBase false"() {
        given:
        List<Invoice> listOfTestedInvoice =listOfInvoiceToTest()
        fileBasedDataBase.delete(1)
        fileBasedDataBase.delete(0)
        when: "get all"
        List<Invoice> listOfInvoice = fileBasedDataBase.getAll()
        then:
        fileBasedDataBase.getAll().get(0) == null;

    }

    def "test update FileBase_Existing_Invoice"() {

        given: "createne new invoice"
        Invoice invoice4 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice4.buyer.id = "Updated Company"
        invoice4.id = 1

        when: "update invoice in base with new invoice"
        fileBasedDataBase.update(1, invoice4)

        then: " compare updated invoice in base with new invoice"
        fileBasedDataBase.getById(1).get() == invoice4

    }


    def "test delete true "() {

        when: "delete invoice"
        boolean isDelete = fileBasedDataBase.delete(1)

        then: " check is function return true after delete  "
        isDelete
    }




//    def "test get new invoices with every invoice new"() {
//
//        when: "get new invoices"
//        List<Invoice> newInvoices = fileBasedDataBase.getNewInvoices()
//        then: " get from list added invoice and assert to added one"
//        newInvoices == fileBasedDataBase.getAll()
//
//    }


//    def "test write to base"() {
//
//        given: " construct file service"
//        List<Invoice> listFromBase = fileBasedDataBase.getAll()
//        when: "get write to base"
//        fileBasedDataBase.writeToBase()
//        String stringReaded = fileBasedDataBase
//                .fileService
//                .readDataFromFile()
//        List<Invoice> listFromFile = fileBasedDataBase
//                .jsonService
//                .convertToInvoices(stringReaded)
//        then:
//        listFromFile == listFromBase
//
//        cleanup:
//        "delete files"()
//    }

    def "delete files"() {
        try {
            Files.delete(Path.of("DataBaseTest.txt"))
            Files.delete(Path.of("InvoiceIdTest.txt"))
        } catch (IOException exception) {
            exception.printStackTrace()
        }
    }

    void setupSpec() {

    }

}
