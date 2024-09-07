package pl.futurecollars.invoicing.controller

import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.bind.annotation.RequestBody
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.Tax
import pl.futurecollars.invoicing.service.JsonService
import pl.futurecollars.invoicing.service.TaxCalculatorService
import spock.lang.Specification
import spock.lang.Subject
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class TaxCalculatorControllerTest extends TestHelpers {

    @Autowired
    MockMvc mockMvc
    @Autowired
    JsonService jsonService

    def "test tax true"() {
        given:
        Invoice invoice1 = createFirstInvoice(createFirstCompany(), createSecondCompany())
        Invoice invoice2 = createSecondInvoice(createSecondCompany(), createFirstCompany())
        invoice1.setId(1)
        invoice1.setId(2)
        String invoiceInJson1 = jsonService.convertToJson(invoice1)
        String invoiceInJson2 = jsonService.convertToJson(invoice2)
        mockMvc.perform(post("/invoices/add").content(invoiceInJson1).contentType(MediaType.APPLICATION_JSON))
        mockMvc.perform(post("/invoices/add").content(invoiceInJson2).contentType(MediaType.APPLICATION_JSON))
        Tax tax = new Tax()
        tax.setIncomingVat(2.700)
        tax.setOutgoingVat(2.700)
        tax.setIncome(15)
        tax.setCosts(15)
        tax.setEarnings(0)
        tax.setVatToPay(0.000)
        String taxInJson = jsonService.convertToJson(tax)
        def expectedREsult = taxInJson.substring(0,taxInJson.size()-2)
        when:
        def response = mockMvc.perform(get("/taxCalculator/444-444-44-44"))
                .andExpect (status().isAccepted())
                .andReturn()
                .response
                .contentAsString
        then:
        response == expectedREsult
    }

    def "test tax false"() {

        expect:
       mockMvc.perform(get("/taxCalculator/"))
                .andExpect (status().isNotFound())


    }

}

