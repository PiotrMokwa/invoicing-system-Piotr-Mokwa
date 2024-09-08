package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification
import spock.lang.Subject
import pl.futurecollars.invoicing.db.Database;

class TaxCalculatorServiceTest extends TestHelpers {

    @Subject
    TaxCalculatorService taxCalculatorService = new TaxCalculatorService(createFileBase())

    def "test isBayer"() {
        given:
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        when:
        boolean result = taxCalculatorService.isBayer("444-444-44-44").test(invoice1)
        then:
        result
    }

    def "test isSeller"() {
        given:
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())

        System.out.println(invoice1)
        System.out.println(invoice2)
        when:
        boolean result = taxCalculatorService.isSeller("555-555-55-55").test(invoice1)
        then:
        result
    }

    def "test incomingVat"() {
        when:
        BigDecimal result = taxCalculatorService.incomingVat("444-444-44-44")
        then:
        result == 2.700
    }

    def "test outgoingVat"() {
        when:
        BigDecimal result = taxCalculatorService.incomingVat("444-444-44-44")
        then:
        result == 2.700
    }

def "test income"(){
    when:
    BigDecimal result = taxCalculatorService.income("444-444-44-44")
    then:
    result == 15

}
    def "test costs"(){
        when:
        BigDecimal result = taxCalculatorService.costs("444-444-44-44")
        then:
        result == 15
    }

    def "test earnings"(){
        when:
        BigDecimal result = taxCalculatorService.earnings("444-444-44-44")
        then:
        result == 0
    }

    def "test vatPayment"(){
        when:
        BigDecimal result = taxCalculatorService.vatPayment("444-444-44-44")
        then:
        result == 0
    }
}
