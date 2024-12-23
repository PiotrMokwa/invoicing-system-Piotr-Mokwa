package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Subject
import java.nio.file.Files
import java.nio.file.Path

class FileBasedDataBaseTest extends TestHelpers {

    @Subject
    FileBasedDataBase<Invoice> fileBasedDataBase = createFileBase(Invoice.class)


    void setupSpec() {
        "delete files"()
    }


    def "test save to FileBase true "() {

        given: " create third invoice in FileBase"
        Invoice thirdInvoice = createSecondInvoice()
        thirdInvoice.getSeller().setId(null)
        thirdInvoice.getBuyer().setId(null)
        thirdInvoice.setId(3)
        when: "save"
        fileBasedDataBase.save(thirdInvoice)
        Invoice thirdInvoiceFromBase = fileBasedDataBase
                .getById(3)
        then: " compare added invoice with invoice from base"
        thirdInvoiceFromBase == thirdInvoice
    }


    def "test get by ID from FileBase"() {

        given: " create the same invoice as in the base"
        Invoice invoice2 = createSecondInvoice()
        invoice2.getBuyer().setId(null)
        invoice2.getSeller().setId(null)
        invoice2.setId(2)
        invoice2.getListOfInvoiceEntry().get(0).setExpansForCar(firstTestCar())
        when: "get By Id"
        Invoice secondInvoiceFromBase = fileBasedDataBase
                .getById(2)
        then: " compare created invice with invoice geted from base"
        secondInvoiceFromBase == invoice2
    }

    def "test get all from FileBase true"() {
        given:
        List<Invoice> listOfTestedInvoice = listOfInvoiceToTest()
        when: "get all"
        List<Invoice> listOfInvoice = fileBasedDataBase.getAll()
        then:
        listOfTestedInvoice == listOfInvoice

    }

    def "test get all from FileBase false"() {
        given:
        fileBasedDataBase.delete(2)
        fileBasedDataBase.delete(1)
        expect:
        try {
            fileBasedDataBase.getAll().get(0) == null
        } catch (IndexOutOfBoundsException exception) {
            exception.printStackTrace()
        }

    }

    def "test update FileBase_Existing_Invoice"() {

        given: "createne new invoice"
        Invoice invoice4 = createSecondInvoice()
        invoice4.buyer.taxIdentification = "Updated Company"
        invoice4.id = 1
        when: "update invoice in base with new invoice"
        fileBasedDataBase.update(1, invoice4)
        then: " compare updated invoice in base with new invoice"
        fileBasedDataBase.getById(1) == invoice4

    }

    def "test delete true "() {

        when: "delete invoice"
        boolean isDelete = fileBasedDataBase.delete(1)
        then: " check is function return true after delete  "
        isDelete
    }

    def "test delete false "() {

        when: "delete invoice"
        Invoice deletedInvoice = fileBasedDataBase.delete(3)
        then: " check is function return null after delete  "
        deletedInvoice == null
    }

    def "delete files"() {
        try {
            Files.delete(Path.of("DataBaseTest.txt"))
            Files.delete(Path.of("InvoiceIdTest.txt"))
        } catch (IOException exception) {
            exception.printStackTrace()
        }
    }

    void cleanup() {
        "delete files"()
    }

}
