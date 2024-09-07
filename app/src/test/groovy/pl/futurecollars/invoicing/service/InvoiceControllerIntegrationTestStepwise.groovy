package pl.futurecollars.invoicing.service

import org.spockframework.spring.EnableSharedInjection
import org.springframework.http.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Shared
import spock.lang.Stepwise

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
@EnableSharedInjection
@Stepwise
class InvoiceControllerIntegrationTestStepwise extends TestHelpers {

    @Shared
    @Autowired
    private MockMvc mockMvc
    @Shared
    @Autowired
    private JsonService jsonService

    def setupSpec() {
        "delete all invoices"(mockMvc, jsonService)
    }

    Invoice "Invoice to test"() {
        def invoice = createFirstInvoice(createFirstCompany(), createSecondCompany())
        invoice.id = 1;
        return invoice
    }

    def "AddInvoice"() {
        given:
        def inviceCheck = mockMvc
                .perform(get("/invoices/GET Invoices"))
                .andReturn()
                .response
                .contentAsString
        def invoice = "Invoice to test"()

        def invoiceAsJson = jsonService.convertToJson(invoice)
        def invoiceAsJsonWithoutLastSign = invoiceAsJson.substring(0, invoiceAsJson.size() - 2)
        when:
        def addedInvoiceId = mockMvc
                .perform(post("/invoices/add")
                        .content(invoiceAsJsonWithoutLastSign)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .response
                .contentAsString

        def addedInvoice = mockMvc
                .perform(get("/invoices/" + addedInvoiceId)
                        .content(invoiceAsJsonWithoutLastSign)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn()
                .response
                .contentAsString
        System.out.println(addedInvoice)
        def invoiceFromBase = jsonService.convertToInvoices("[" + addedInvoice + "]").get(0)
        invoice.setId(invoiceFromBase.getId())
        then:
        invoiceFromBase == invoice
    }

    def "GetInvoices"() {
        given:
        def invoice = "Invoice to test"()
        when:
        def response = mockMvc
                .perform(get("/invoices/GET Invoices"))
                .andExpect(status().isAccepted())
                .andReturn()
                .response
                .contentAsString
        def responsInJSon = jsonService.convertToInvoices(response)[0]
        invoice.setId(responsInJSon.getId())
        then:
        responsInJSon == invoice
    }

    def "GetInvoice"() {
        given:
        def invoice = "Invoice to test"()
        when:
        def response = mockMvc
                .perform(get("/invoices/1"))
                .andExpect(status().isAccepted())
                .andReturn()
                .response
                .contentAsString

        def responseInJSon = jsonService.convertToInvoices("[" + response + "]")[0]
        then:
        responseInJSon == invoice
    }

    def "UpdateInvoice"() {
        given:
        def newInvoice = "Invoice to test"()
        newInvoice.setDate(LocalDate.now().minusDays(10))
        def newInvoiceAsJson = jsonService.convertToJson(newInvoice)
        when:
        def updatedInvoice = mockMvc
                .perform(
                        put("/invoices/update/1")
                                .content(newInvoiceAsJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .response
                .contentAsString

        def updatedInvoiceInJson = jsonService.convertToInvoices("[" + updatedInvoice + "]").get(0)
        updatedInvoiceInJson.id = 1
        then:
        updatedInvoiceInJson == newInvoice
    }

    def "delete Invoice"() {
        given:
        def newInvoice = "Invoice to test"()
        newInvoice.setDate(LocalDate.now().minusDays(10))
//        String invoiceAsJson = jsonService.convertToJson

        when:
        def response = mockMvc.perform(delete("/invoices/delete/1"))
                .andExpect(status().isAccepted())
                .andReturn()
                .response
                .contentAsString
        then:
        jsonService.convertToInvoices("[" + response + "]")[0] == newInvoice

    }

    def cleanupSpec() {

        //Clean File Base
        deleteFilesBase(baseTestFileSpring, baseIdTestFileSpring)

    }


}
