package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.TaxValues
import pl.futurecollars.invoicing.service.JsonService
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("inMemoryBase")
@SpringBootTest
@AutoConfigureMockMvc
class TaxCalculatorControllerTest extends TestHelpers {

    @Autowired
    MockMvc mockMvc
    @Autowired
    JsonService jsonService

    def setup() {
        "delete all invoices"(mockMvc, jsonService)
        "add invoices to base"()
    }

    def "add invoices to base"() {
        Invoice invoice1 = createFirstInvoice(createSecondCompany(), createFirstCompany())
        Invoice invoice2 = createSecondInvoice(createFirstCompany(), createSecondCompany())
        invoice1.setId(1)
        invoice1.setId(2)
        String invoiceInJson1 = jsonService.convertToJson(invoice1)
        String invoiceInJson2 = jsonService.convertToJson(invoice2)
        mockMvc.perform(post("/invoices/add").content(invoiceInJson1).contentType(MediaType.APPLICATION_JSON))
        mockMvc.perform(post("/invoices/add").content(invoiceInJson2).contentType(MediaType.APPLICATION_JSON))

    }

    def "create TaxValues"() {
        def income = BigDecimal.valueOf(76011.62).setScale(2)
        def costs = BigDecimal.valueOf(11729.47).setScale(2)
        def earnings = BigDecimal.valueOf(64282.15).setScale(2)
        def pensionInsurance = BigDecimal.valueOf(514.57).setScale(2)
        def taxCalculationBase = BigDecimal.valueOf(63767.58).setScale(2)
        def taxCalculationBaseRound = BigDecimal.valueOf(63768).setScale(2)
        def incomeTax = BigDecimal.valueOf(12115.92).setScale(2)
        def healthInsuranceValue = BigDecimal.valueOf(319.94).setScale(2)
        def healthInsuranceValueForTax = BigDecimal.valueOf(275.50).setScale(2)
        def finalIncomeTaxValue = BigDecimal.valueOf(11840).setScale(2)

        return TaxValues.builder()
                .income(income)
                .costs(costs)
                .incomeSubtractCosts(earnings)
                .pensionInsurance(pensionInsurance)
                .taxCalculationBase(taxCalculationBase)
                .taxCalculationBaseRound(taxCalculationBaseRound)
                .incomeTax(incomeTax)
                .healthInsuranceValue(healthInsuranceValue)
                .healthInsuranceValueForTax(healthInsuranceValueForTax)
                .finalIncomeTaxValue(finalIncomeTaxValue)
                .build()
    }


    def "test tax true"() {
        given:

        String taxValuesInJson = jsonService.convertToJson("create TaxValues"())
        def expectedResult = taxValuesInJson.substring(0, taxValuesInJson.size() - 2)
        def company = jsonService.convertToJson(createFirstCompany())
        def expectedCompany = company.substring(0, company.size() - 2)

        when:
        def response = mockMvc.perform(
                post("/taxCalculator/company")
                        .content(expectedCompany).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn()
                .response
                .contentAsString
        then:
        response == expectedResult
    }

    def "test tax false"() {

        expect:
        mockMvc.perform(post("/taxCalculator/"))
                .andExpect(status().isNotFound())
    }
}

