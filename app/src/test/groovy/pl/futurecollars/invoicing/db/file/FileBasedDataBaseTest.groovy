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

    static FileBasedDataBase createFileBase() {
        InvoiceSetup invoiceSetup = invoiceSetup()
        FileBasedDataBase fileBasedDataBase = new FileBasedDataBase(invoiceSetup)
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        fileBasedDataBase.save(invoice1)
        fileBasedDataBase.save(invoice2)
        return fileBasedDataBase
    }

    def "test save to FileBase ()"() {

        given: " create third invoice in FileBase"
        Invoice thirdInvoice = createFirstInvoice(
                createFirstCompany(), createSecondCompany()
        )
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
        then:" compare created invice with invoice geted from base"
        secondInvoiceFromBase.get() == invoice2
    }

    def "test get all from FileBase"() {

        when: "get all"
        List<Invoice> listOfInvoice = fileBasedDataBase.getAll()
        then:
        listOfInvoice == fileBasedDataBase.invoices.values().asList()

    }

    def "test update FileBase"() {

        given:"createne new invoice"
        Invoice invoice4 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice4.buyer.id = "Updated Company"
        invoice4.id = 2

        when: "update invoice in base with new invoice"
        fileBasedDataBase.update(2, invoice4)

        then:" compare updated invoice in base with new invoice"
        fileBasedDataBase.invoices.get(2) == invoice4

    }

    def "test delete "() {

        when: "delete invoice"
        boolean isDelete = fileBasedDataBase.delete(2)
        boolean isEmpty = fileBasedDataBase.getById(2).isEmpty()
        then:" check is function return true after delete and if base contain deleted invoice "
        isDelete & isEmpty
    }

    def "test get new invoices with every invoice new"() {

        when: "get new invoices"
        List<Invoice> newInvoices = fileBasedDataBase.getNewInvoices()
        then: " get from list added invoice and assert to added one"
        newInvoices == fileBasedDataBase.getAll()

    }

    def "test get new invoices with no new invoices"() {

        given: " write base to file"
        fileBasedDataBase.writeToBase()
        when: "get new invoices"
        List<Invoice> newInvoices = fileBasedDataBase.getNewInvoices()
        then: " check do we have new invoices"
        newInvoices.isEmpty()
        cleanup:
        "delete files"()
    }

    def "test write to base"() {

        given: " construct file service"
        List<Invoice> listFromBase = fileBasedDataBase.getAll()
        when: "get write to base"
        fileBasedDataBase.writeToBase()
        String stringReaded = fileBasedDataBase
                .fileService
                .readDataFromFile()
        List<Invoice> listFromFile = fileBasedDataBase
                .jsonService
                .convertToInvoices(stringReaded)
        then:
        listFromFile == listFromBase

        cleanup:
        "delete files"()
    }

    def "test print from base"() {
        given: "write data to base, read invoices from base"
        fileBasedDataBase.writeToBase()
        String closeSymbolCollectionJason = " ]"
        List<Invoice> listofInvoices = fileBasedDataBase
                .jsonService
                .convertToInvoices(
                        fileBasedDataBase
                                .fileService
                                .readDataFromFile() + closeSymbolCollectionJason)
        when: "print from base"
        List<Invoice> result = fileBasedDataBase.printFromBase()
        then: " is list of invoices the same as reading from Base"
        listofInvoices == result
        cleanup:
        "delete files"()
    }

    def "delete files"() {
        try {
            Files.delete(Path.of("DataBaseTest"))
            Files.delete(Path.of("InvoiceIdTest"))
        } catch (IOException exception) {
            exception.printStackTrace()
        }
    }

    void setupSpec() {

    }

}
