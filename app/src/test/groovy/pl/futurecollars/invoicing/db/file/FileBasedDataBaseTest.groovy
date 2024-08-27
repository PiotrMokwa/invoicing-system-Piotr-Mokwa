package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Subject
import java.nio.file.Files
import java.nio.file.Path

class FileBasedDataBaseTest extends TestHelpers {

    @Subject
    FileBasedDataBase fileBasedDataBase = createFileBase()


    void setupSpec() {
        "delete files"()
    }


    def "test save to FileBase ()"() {

        given: " create third invoice in FileBase"
        Invoice thirdInvoice = createSecondInvoice(createSecondCompany(), createFirstCompany())
        thirdInvoice.setId(3)
        when: "save"

        fileBasedDataBase.save(thirdInvoice)
        Invoice thirdInvoiceFromBase = fileBasedDataBase
                .getById(3)
                .get()
        then: " compare added invoice with invoice from base"
        thirdInvoiceFromBase == thirdInvoice
    }

    def "test get by ID from FileBase"() {

        given: " create the same invoice as in the base"
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice2.setId(2)
        when: "get By Id"
        Optional secondInvoiceFromBase = fileBasedDataBase
                .getById(2)
        then: " compare created invice with invoice geted from base"
        secondInvoiceFromBase.get() == invoice2
    }

    def "test get all from FileBase true"() {
        given:
        List<Invoice> listOfTestedInvoice =listOfInvoiceToTest()

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
        }catch (IndexOutOfBoundsException exception){
            exception.printStackTrace()
        }

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

    def "delete files"() {
        try {
            Files.delete(Path.of("DataBaseTest.txt"))
            Files.delete(Path.of("InvoiceIdTest.txt"))
        } catch (IOException exception) {
            exception.printStackTrace()
        }
    }

    void cleanup(){
        "delete files"()
    }

}
