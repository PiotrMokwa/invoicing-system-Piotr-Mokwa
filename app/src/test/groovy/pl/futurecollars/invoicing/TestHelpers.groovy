package pl.futurecollars.invoicing

import lombok.Builder
import lombok.Data
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import pl.futurecollars.invoicing.model.Car
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
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice1.setId(1)
        invoice2.setId(2)
        invoice1.getListOfInvoiceEntry().get(0).setCar(firstTestCar())
        invoice2.getListOfInvoiceEntry().get(0).setCar(firstTestCar())
        fileBasedDataBase.save(invoice1)
        fileBasedDataBase.save(invoice2)
        return fileBasedDataBase
    }

    static InMemoryDatabase inMemoryDatabase() {

        Map<Integer, Invoice> invoices = new HashMap<>()
        InMemoryDatabase inMemoryDatabase = new InMemoryDatabase(1, invoices)
        Invoice invoice1 = createFirstInvoice(createSecondCompany(), createFirstCompany())
        Invoice invoice2 = createSecondInvoice(createFirstCompany(), createSecondCompany())
        invoice1.setId(1)
        invoice1.setId(2)
        inMemoryDatabase.save(invoice1)
        inMemoryDatabase.save(invoice2)
        return inMemoryDatabase
    }

    static List<Invoice> listOfInvoiceToTest() {
        List<Invoice> listOfTestedInvoice = new LinkedList<>();
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice1.setId(1)
        invoice2.setId(2)
        invoice1.getListOfInvoiceEntry().get(0).setCar(firstTestCar())
        invoice2.getListOfInvoiceEntry().get(0).setCar(firstTestCar())
        listOfTestedInvoice.add(invoice1)
        listOfTestedInvoice.add(invoice2)
        return listOfTestedInvoice
    }

    static Car firstTestCar() {
        Car car = new Car()
        car.setCarRegistrationNumber("KR 4523")
        car.setPrivateUse(false)
        return car
    }

    static Car secondTestCar() {
        Car car = new Car()
        car.setCarRegistrationNumber("BKK 4523")
        car.setPrivateUse(true)
        return car
    }

    static Company createFirstCompany() {
        BigDecimal pensionInsurance = 514.57
        BigDecimal healthInsuranceBaseValue = 3554.89
        BigDecimal amountOfHealthInsurance = (healthInsuranceBaseValue * BigDecimal.valueOf(0.09))
                .setScale(2, RoundingMode.DOWN);
        BigDecimal amountOfHealthInsuranceToReduceTax = healthInsuranceBaseValue.multiply(BigDecimal.valueOf(0.0775))
                .setScale(2, RoundingMode.DOWN);

        return Company.builder()
                .id("1111")
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
                .id("2222")
                .taxIdentification("555-555-55-55")
                .name("PGA")
                .address("PoznaĹ„, street Ĺ»eglarska")
                .pensionInsurance(pensionInsurance)
                .healthInsuranceBaseValue(healthInsuranceBaseValue)
                .amountOfHealthInsurance(amountOfHealthInsurance)
                .amountOfHealthInsuranceToReduceTax(amountOfHealthInsuranceToReduceTax)
                .build()

    }

    static Invoice createFirstInvoice(Company bayer, Company seller) {

        List<InvoiceEntry> itemsList = new ArrayList<>()
        itemsList.add(createFirstInvoiceEntry())
        itemsList.add(createSecondInvoiceEntry())
        return new Invoice(LocalDate.now(), bayer, seller, itemsList)
    }

    static Invoice createSecondInvoice(Company bayer, Company seller) {

        List<InvoiceEntry> itemsList = new ArrayList<>()
        itemsList.add(createThirdInvoiceEntry())
        itemsList.add(createForthInvoiceEntry())
        return new Invoice(LocalDate.now(), bayer, seller, itemsList)
    }

    static InvoiceEntry createFirstInvoiceEntry() {

        InvoiceEntry invoiceEntry = new InvoiceEntry()
        invoiceEntry.setDescription("Ream of paper")
        BigDecimal price = BigDecimal.valueOf(70000.00)
        Vat vatRate = Vat.vat_23
        BigDecimal vatValue = price * vatRate.getVatValue().setScale(2, RoundingMode.UP)
        invoiceEntry.setPrice(price)
        invoiceEntry.setVatRate(Vat.vat_23)
        invoiceEntry.setVatValue(vatValue)

        return invoiceEntry
    }

    static InvoiceEntry createSecondInvoiceEntry() {

        InvoiceEntry invoiceEntry = new InvoiceEntry()
        invoiceEntry.setDescription("Toner")
        BigDecimal price = BigDecimal.valueOf(6011.62)
        Vat vatRate = Vat.vat_8
        BigDecimal vatValue = price * vatRate.getVatValue().setScale(2, RoundingMode.UP)
        invoiceEntry.setPrice(price)
        invoiceEntry.setVatRate(Vat.vat_8)
        invoiceEntry.setVatValue(vatValue)

        return invoiceEntry
    }

    static InvoiceEntry createThirdInvoiceEntry() {

        InvoiceEntry invoiceEntry = new InvoiceEntry()
        invoiceEntry.setDescription("Toner")
        BigDecimal price = BigDecimal.valueOf(10000.00)
        Vat vatRate = Vat.vat_8
        BigDecimal vatValue = price * vatRate.getVatValue().setScale(2, RoundingMode.UP)
        invoiceEntry.setPrice(price)
        invoiceEntry.setVatRate(Vat.vat_8)
        invoiceEntry.setVatValue(vatValue)
        invoiceEntry.setCar(secondTestCar())
        return invoiceEntry
    }

    static InvoiceEntry createForthInvoiceEntry() {

        InvoiceEntry invoiceEntry = new InvoiceEntry()
        invoiceEntry.setDescription("Toner")
        BigDecimal price = BigDecimal.valueOf(1329.47)
        Vat vatRate = Vat.vat_8
        BigDecimal vatValue = price * vatRate.getVatValue().setScale(2, RoundingMode.UP)
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
            try {
                jsonService.convertToInvoices(invoiceList).forEach(invoice -> {
                    mockMvc.perform(delete("/invoices/delete/" + invoiceIndex))
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

}
