package pl.futurecollars.invoicing

import lombok.Data
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import pl.futurecollars.invoicing.service.JsonService
import pl.futurecollars.invoicing.setup.InvoiceSetup
import pl.futurecollars.invoicing.db.file.FileBasedDataBase
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@Data
abstract class TestHelpers extends Specification {
    static String baseTestFile = "DataBaseTest.txt"
    static String baseIdTestFile = "InvoiceIdTest.txt"
    static String baseTestFileSpring = "SpringBase.txt"
    static String baseIdTestFileSpring = "SpringId.txt"

    static InvoiceSetup invoiceSetup() {
        InvoiceSetup invoiceSetup = new InvoiceSetup(baseTestFile, baseIdTestFile)
        deleteFilesBase(baseTestFile, baseIdTestFile)
        createEmptyFilesBase(baseTestFile, baseIdTestFile)
        return invoiceSetup
    }

    static deleteFilesBase(String basePath, String idPath) {

        Files.deleteIfExists(Path.of(basePath))
        Files.deleteIfExists(Path.of(idPath))

    }

    static createEmptyFilesBase(String basePath, String idPath) {
        Files.createFile(Path.of(basePath))
        Files.createFile(Path.of(idPath))
        Files.write(Path.of(idPath), "1".getBytes(), StandardOpenOption.WRITE);
    }


    static FileBasedDataBase createFileBase() {
        InvoiceSetup invoiceSetup = invoiceSetup()
        FileBasedDataBase fileBasedDataBase = new FileBasedDataBase(invoiceSetup)
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice1.setId(1)
        invoice1.setId(2)
        fileBasedDataBase.save(invoice1)
        fileBasedDataBase.save(invoice2)
//        System.out.println(fileBasedDataBase.getAll())
        return fileBasedDataBase
    }

    static InMemoryDatabase inMemoryDatabase() {
//        InvoiceSetup invoiceSetup = invoiceSetup()
        Map<Integer, Invoice> invoices = new HashMap<>()
        InMemoryDatabase inMemoryDatabase = new InMemoryDatabase(1, invoices)
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice1.setId(1)
        invoice1.setId(2)
        inMemoryDatabase.save(invoice1)
        inMemoryDatabase.save(invoice2)
//        System.out.println(fileBasedDataBase.getAll())
        return inMemoryDatabase
    }

    static List<Invoice> listOfInvoiceToTest() {
        List<Invoice> listOfTestedInvoice = new LinkedList<>();
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        listOfTestedInvoice.add(invoice1)
        invoice1.setId(1)
        listOfTestedInvoice.add(invoice2)
        invoice2.setId(2)
        return listOfTestedInvoice
    }


    static Company createFirstCompany() {

        return new Company("1111", "444-444-44-44", "Warszawa, street Marynarska")
    }

    static Company createSecondCompany() {

        return new Company("2222", "555-555-55-55", "PoznaĹ„, street Ĺ»eglarska")
    }

    static Invoice createFirstInvoice(Company bayer, Company seller) {

        List<InvoiceEntry> itemsList = new ArrayList<>()
        itemsList.add(createFirstInvoiceEntry())
        itemsList.add(createSecondInvoiceEntry())
        return new Invoice(LocalDate.now(), bayer, seller, itemsList)
    }

    static Invoice createSecondInvoice(Company bayer, Company seller) {

        List<InvoiceEntry> itemsList = new ArrayList<>()
        itemsList.add(createSecondInvoiceEntry())
        itemsList.add(createFirstInvoiceEntry())
        return new Invoice(LocalDate.now(), bayer, seller, itemsList)
    }

    static InvoiceEntry createFirstInvoiceEntry() {

        InvoiceEntry invoiceEntry = new InvoiceEntry()
        invoiceEntry.setDescription("Ream of paper")
        BigDecimal price = BigDecimal.valueOf(10)
        Vat vatRate = Vat.vat_23
        BigDecimal vatValue = price * vatRate.getVatValue()
        invoiceEntry.setPrice(price)
        invoiceEntry.setVatRate(Vat.vat_23)
        invoiceEntry.setVatValue(vatValue)
        return invoiceEntry
    }

    static InvoiceEntry createSecondInvoiceEntry() {

        InvoiceEntry invoiceEntry = new InvoiceEntry()
        invoiceEntry.setDescription("Toner")
        BigDecimal price = BigDecimal.valueOf(5)
        Vat vatRate = Vat.vat_8
        BigDecimal vatValue = price * vatRate.getVatValue()
        invoiceEntry.setPrice(price)
        invoiceEntry.setVatRate(Vat.vat_8)
        invoiceEntry.setVatValue(vatValue)
        return invoiceEntry
    }

    void "delete all invoices"(MockMvc mockMvc, JsonService jsonService) {
        def invoiceList = mockMvc.perform(get("/invoices/GET Invoices"))
                .andReturn()
                .response
                .contentAsString
        int invoiceIndex = 1;
        if (!invoiceList.isEmpty()) {
            System.out.println("invoices list before delet: " + invoiceList)
            System.out.println(invoiceIndex)
           try{
                jsonService.convertToInvoices(invoiceList).forEach(invoice -> {
                    mockMvc.perform(delete("/invoices/delete/" + invoiceIndex ))
                    invoiceIndex++
                })
                }catch(Exception exception){
               System.out.println(exception)
               }

           def afterDelete = mockMvc.perform(get("/invoices/GET Invoices"))
                    .andReturn()
                    .response
                    .contentAsString
            System.out.println("list of invoices after delete: " +afterDelete)

        }
    }


//static boolean isFileBaseExist(){
//
//    return Files.exists(Path.of("DataBaseTest.txt"))
//}
//
//    static deleteFilesBase(){
//
//        Files.deleteIfExists(Path.of("DataBaseTest.txt"))
////        Files.deleteIfExists(Path.of("InvoiceIdTest.txt"))
////
////    }
////    static createEmptyFilesBase(){
////        Files.createFile(Path.of("DataBaseTest.txt"))
////        Files.createFile(Path.of("InvoiceIdTest.txt"))
////        Files.writeString(Path.of("InvoiceIdTest.txt"), "1".getBytes());
////        Files.writeString((Path.of("ccc.txt")), "1".getBytes());
////    }
}
