package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Shared
import spock.lang.Subject

class TaxCalculatorServiceTest extends TestHelpers {

    @Shared
    @Subject
    TaxCalculatorService taxCalculatorService = new TaxCalculatorService(inMemoryDatabase())


    def setupSpec() {
        "setupCompany"()
        taxCalculatorService.company.setTaxIdentification("444-444-44-44")
        "display base"()
    }

    def "display base"() {
        def i = 1
        taxCalculatorService.getDataBase().getAll()
                .forEach(invoice -> {
                    System.out.println("Bayer " + invoice.getBuyer())
                    System.out.println("Seller " + invoice.getSeller())
                    System.out.println("invoice entry nr " + i)
                    i++;
                    System.out.println(invoice.getListOfInvoiceEntry()
                            .forEach(value -> {
                                System.out.println("Price " + value.getPrice())
                                System.out.println("Vat Value " + value.getVatValue())
                                if (!(value.getCar() == null)) {
                                    System.out.println("Car Private use " + value.getCar().privateUse)
                                } else {
                                    System.out.println("Car empty")
                                }
                            })
                    )
                }
                )
    }

    def "setupCompany"() {
        taxCalculatorService.setCompany(createFirstCompany())

    }


    def "test isBayer"() {
        given:
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())

        when:
        boolean result = taxCalculatorService.isBayer().test(invoice1)
        then:
        result
    }

    def "test isSeller"() {
        given:
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        when:
        boolean result = taxCalculatorService.isSeller().test(invoice2)
        then:
        result
    }

    def "test incomingVat"() {
        when:
        System.out.println(taxCalculatorService.dataBase.getAll())
        BigDecimal result = taxCalculatorService.incomingVat()
        then:
        result == 16580.93
    }

    def "test outgoingVat"() {
        when:
        BigDecimal result = taxCalculatorService.outgoingVat()
        then:
        result == 506.36
    }

    def "test income"() {
        when:
        BigDecimal result = taxCalculatorService.income()
        then:
        result == 76011.62

    }

    def "test costs"() {

        when:
        BigDecimal result = taxCalculatorService.costs()
        then:
        result == 11729.47
    }

    def "test earnings"() {
        when:
        BigDecimal result = taxCalculatorService.earnings()
        then:
        result == 64282.15
    }

    def "test vatPayment"() {
        when:
        BigDecimal result = taxCalculatorService.vatPayment()
        then:
        result == 16074.57
    }

    def "test tax Calculation Base"() {

        when:
        BigDecimal result = taxCalculatorService.taxCalculationBase()
        then:
        result == 63767.58
    }

    def "test tax Calculation Base round "() {

        when:
        BigDecimal result = taxCalculatorService.roundedTaxCalculationBase()
        then:
        result == 63768.00
    }

    def "test income Tax "() {

        when:
        BigDecimal result = taxCalculatorService.incomeTax()
        then:
        result == 12115.92
    }

    def "test final Income Tax Value "() {

        when:
        BigDecimal result = taxCalculatorService.finalIncomeTaxValue()
        then:
        result == 11840.00
    }

}