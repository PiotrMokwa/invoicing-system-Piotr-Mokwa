package pl.futurecollars.invoicing

import lombok.Builder
import lombok.Data
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import pl.futurecollars.invoicing.model.Car
import pl.futurecollars.invoicing.model.Tax
import pl.futurecollars.invoicing.service.JsonService
import pl.futurecollars.invoicing.setup.InvoiceSetup
import pl.futurecollars.invoicing.db.file.FileBasedDataBase
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat
import spock.lang.Specification
import java.math.RoundingMode
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@Builder
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
        Invoice invoice1 = createFirstInvoice()
        Invoice invoice2 = createSecondInvoice()
        invoice1.setId(1)
        invoice2.setId(2)
        invoice1.getListOfInvoiceEntry().get(0).setExpansForCar(firstTestCar())
        invoice2.getListOfInvoiceEntry().get(0).setExpansForCar(firstTestCar())
        fileBasedDataBase.save(invoice1)
        fileBasedDataBase.save(invoice2)
        return fileBasedDataBase
    }

    static InMemoryDatabase inMemoryDatabase() {

        Map<Integer, Invoice> invoices = new HashMap<>()
        InMemoryDatabase inMemoryDatabase = new InMemoryDatabase(1, invoices)
        Invoice invoice1 = createFirstInvoice()
        Invoice invoice2 = createSecondInvoice()
        invoice1.setId(1)
        invoice1.setId(2)
        inMemoryDatabase.save(invoice1)
        inMemoryDatabase.save(invoice2)
        return inMemoryDatabase
    }

    static List<Invoice> listOfInvoiceToTest() {
        List<Invoice> listOfTestedInvoice = new LinkedList<>();
        Invoice invoice1 = createFirstInvoice()
        Invoice invoice2 = createSecondInvoice()
        invoice1.setId(1)
        invoice2.setId(2)
        invoice1.getListOfInvoiceEntry().get(0).setExpansForCar(firstTestCar())
        invoice2.getListOfInvoiceEntry().get(0).setExpansForCar(firstTestCar())
        listOfTestedInvoice.add(invoice1)
        listOfTestedInvoice.add(invoice2)
        return listOfTestedInvoice
    }

    static Car firstTestCar() {

        return Car.builder()
                .isPrivateUse(false)
                .carRegistrationNumber("KR 4523")
                .build()
    }

    static Car secondTestCar() {

        return Car.builder()
                .isPrivateUse(true)
                .carRegistrationNumber("BKK 4523")
                .build()
    }

    static Company createFirstCompany() {
        BigDecimal pensionInsurance = 514.57
        BigDecimal healthInsuranceBaseValue = 3554.89
        BigDecimal amountOfHealthInsurance = (healthInsuranceBaseValue * BigDecimal.valueOf(0.09))
                .setScale(2, RoundingMode.DOWN);
        BigDecimal amountOfHealthInsuranceToReduceTax = healthInsuranceBaseValue.multiply(BigDecimal.valueOf(0.0775))
                .setScale(2, RoundingMode.DOWN);

        return Company.builder()
                .id(null)
                .taxIdentification("444-444-44-44")
                .name("ORLEN")
                .address("Warszawa, street Marynarska")
                .pensionInsurance(pensionInsurance)
                .healthInsuranceBaseValue(healthInsuranceBaseValue)
                .amountOfHealthInsurance(amountOfHealthInsurance)
                .amountOfHealthInsuranceToReduceTax(amountOfHealthInsuranceToReduceTax)
                .build()

    }

    static Company createSecondCompany() {
        BigDecimal pensionInsurance = 514.57
        BigDecimal healthInsuranceBaseValue = 3554.89
        BigDecimal amountOfHealthInsurance = (healthInsuranceBaseValue * BigDecimal.valueOf(0.09))
                .setScale(2, RoundingMode.DOWN);
        BigDecimal amountOfHealthInsuranceToReduceTax = (healthInsuranceBaseValue * BigDecimal.valueOf(0.0775))
                .setScale(2, RoundingMode.DOWN);

        return Company.builder()
                .id(null)
                .taxIdentification("555-555-55-55")
                .name("PGA")
                .address("Poznan, streeet Wesola")
                .pensionInsurance(pensionInsurance)
                .healthInsuranceBaseValue(healthInsuranceBaseValue)
                .amountOfHealthInsurance(amountOfHealthInsurance)
                .amountOfHealthInsuranceToReduceTax(amountOfHealthInsuranceToReduceTax)
                .build()

    }

    static Invoice createFirstInvoice() {

        List<InvoiceEntry> itemsList = new ArrayList<>()
        itemsList.add(createFirstInvoiceEntry())
        itemsList.add(createSecondInvoiceEntry())
        return Invoice.builder()
                .number("PM/2020/01/01/001")
                .date(LocalDate.now())
                .seller(createFirstCompany())
                .buyer(createSecondCompany())
                .listOfInvoiceEntry(itemsList)
                .build()

    }

    static Invoice createSecondInvoice() {

        List<InvoiceEntry> itemsList = new ArrayList<>()
        itemsList.add(createThirdInvoiceEntry())
        itemsList.add(createForthInvoiceEntry())
        return Invoice.builder()
                .number("PM/2020/01/01/002")
                .date(LocalDate.now())
                .seller(createSecondCompany())
                .buyer(createFirstCompany())
                .listOfInvoiceEntry(itemsList)
                .build()
    }

    static InvoiceEntry createFirstInvoiceEntry() {

        BigDecimal quantity = BigDecimal.ONE.setScale(2)
        BigDecimal price = BigDecimal.valueOf(70000.00).setScale(2)
        Vat vatRate = Vat.vat_23
        BigDecimal vatValue = (price * vatRate.getVatValue()).setScale(2, RoundingMode.UP)
        return InvoiceEntry.builder()
                .description("Ream of paper")
                .quantity(quantity)
                .price(price)
                .vatRate(vatRate)
                .vatValue(vatValue)
                .expansForCar(firstTestCar())
                .build()

    }

    static InvoiceEntry createSecondInvoiceEntry() {

        BigDecimal quantity = BigDecimal.ONE.setScale(2)
        BigDecimal price = BigDecimal.valueOf(6011.62)
        Vat vatRate = Vat.vat_8
        BigDecimal vatValue = (price * vatRate.getVatValue()).setScale(2, RoundingMode.UP)
        return InvoiceEntry.builder()
                .description("Toner")
                .quantity(quantity)
                .price(price)
                .vatRate(vatRate)
                .vatValue(vatValue)
                .expansForCar(firstTestCar())
                .build()
    }

    static InvoiceEntry createThirdInvoiceEntry() {

        BigDecimal quantity = BigDecimal.ONE.setScale(2)
        BigDecimal price = BigDecimal.valueOf(10000.00).setScale(2)
        Vat vatRate = Vat.vat_8
        BigDecimal vatValue = (price * vatRate.getVatValue()).setScale(2, RoundingMode.UP)
        return InvoiceEntry.builder()
                .description("Toner")
                .quantity(quantity)
                .price(price)
                .vatRate(vatRate)
                .vatValue(vatValue)
                .expansForCar(secondTestCar())
                .build()
    }

    static InvoiceEntry createForthInvoiceEntry() {

        BigDecimal quantity = BigDecimal.ONE.setScale(2)
        BigDecimal price = BigDecimal.valueOf(1329.47)
        Vat vatRate = Vat.vat_8
        BigDecimal vatValue = (price * vatRate.getVatValue()).setScale(2, RoundingMode.UP)
        return InvoiceEntry.builder()
                .description("Toner")
                .quantity(quantity)
                .price(price)
                .vatRate(vatRate)
                .vatValue(vatValue)
                .expansForCar(firstTestCar())
                .build()
    }


    void "delete all invoices"(MockMvc mockMvc, JsonService jsonService) {
        def invoiceList = mockMvc.perform(get("/invoices/GET Invoices"))
                .andReturn()
                .response
                .contentAsString
        int invoiceIndex = 1;
        if (!invoiceList.isEmpty()) {
            System.out.println("invoices list before delete: " + invoiceList)
            System.out.println(invoiceIndex)
            try {
                jsonService.convertToInvoices(invoiceList).forEach(invoice -> {
                    mockMvc.perform(delete("/invoices/delete/" + invoice.id))
                    invoiceIndex++
                })
            } catch (Exception exception) {
                System.out.println(exception)
            }

            def afterDelete = mockMvc.perform(get("/invoices/GET Invoices"))
                    .andReturn()
                    .response
                    .contentAsString
            System.out.println("list of invoices after delete: " + afterDelete)

        }
    }

    Tax getTaxVatToTest() {
        return Tax.builder()
                .incomingVat(BigDecimal.valueOf(16580.93).setScale(2))
                .outgoingVat(BigDecimal.valueOf(506.36).setScale(2))
                .income(BigDecimal.valueOf(76011.62).setScale(2))
                .costs(BigDecimal.valueOf(11729.47).setScale(2))
                .earnings(BigDecimal.valueOf(64282.15).setScale(2))
                .vatToPay(BigDecimal.valueOf(16074.57).setScale(2))
                .roundedTaxCalculationBase(BigDecimal.valueOf(63768).setScale(2))
                .build()
    }
}
