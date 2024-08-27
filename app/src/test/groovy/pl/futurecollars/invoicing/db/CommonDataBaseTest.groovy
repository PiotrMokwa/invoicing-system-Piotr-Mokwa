package pl.futurecollars.invoicing.db

import pl.futurecollars.invoicing.InvoiceSetup
import pl.futurecollars.invoicing.db.file.FileBasedDataBase
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat
import spock.lang.Specification

import java.time.LocalDate

abstract class CommonDataBaseTest extends Specification {

    static InvoiceSetup invoiceSetup() {
        InvoiceSetup invoiceSetup = new InvoiceSetup("DataBaseTest.txt", "InvoiceIdTest.txt")

        return invoiceSetup
    }

    static FileBasedDataBase createFileBase() {
        InvoiceSetup invoiceSetup = invoiceSetup()
        FileBasedDataBase fileBasedDataBase = new FileBasedDataBase(invoiceSetup)
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        fileBasedDataBase.save(invoice1)
        fileBasedDataBase.save(invoice2)
      System.out.println(fileBasedDataBase.getAll())
        return fileBasedDataBase
    }

    static List<Invoice> listOfInvoiceToTest() {
        List<Invoice> listOfTestedInvoice = new LinkedList<>()
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        listOfTestedInvoice.add(invoice1)
        invoice1.setId(0)
        listOfTestedInvoice.add(invoice2)
        invoice2.setId(1)
        return listOfTestedInvoice
    }


    static Company createFirstCompany() {

        return new Company("1111", 300, "Warszawa, street Marynarska")
    }

    static Company createSecondCompany() {

        return new Company("2222", 400, "Poznań, street Żeglarska")
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

}
