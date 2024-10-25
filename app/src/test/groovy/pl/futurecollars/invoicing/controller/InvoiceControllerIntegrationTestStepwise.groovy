package pl.futurecollars.invoicing.controller

import org.spockframework.spring.EnableSharedInjection
import org.springframework.http.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.JsonService
import spock.lang.Shared
import spock.lang.Stepwise
import static java.util.stream.Collectors.*

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("inMemoryBase")
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
        def invoice = createFirstInvoice()
        invoice.id = 1
        return invoice
    }

    int "get invoice number"(){
        def response = mockMvc
                .perform(get("/invoices/GET Invoices"))
                .andExpect(status().isAccepted())
                .andReturn()
                .response
                .contentAsString
        System.out.println(response)
        def data = jsonService
                .convertToInvoices(response)
                .stream().map(value->value.id).collect(toList())

        return data.get(0)
    }

    def "AddInvoice"() {
        given:
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

        def inviceCheck = mockMvc
                .perform(get("/invoices/GET Invoices"))
                .andReturn()
                .response
                .contentAsString
        System.out.println("alla invoices test get invoices" + inviceCheck)


System.out.println("get invoice number"())
        def addedInvoice = mockMvc
                .perform(get("/invoices/get/" + addedInvoiceId))
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

        def inviceCheck = mockMvc
                .perform(get("/invoices/GET Invoices"))
                .andReturn()
                .response
                .contentAsString
        System.out.println("alla invoices test get invoices" + inviceCheck)

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
        def invoiceNumber = "get invoice number"()
        invoice.setId(invoiceNumber)
        when:
        def response = mockMvc
                .perform(get("/invoices/get/" + invoiceNumber))
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
        def invoiceNumber = "get invoice number"()
        newInvoice.setId(invoiceNumber)
        newInvoice.setDate(LocalDate.now().minusDays(10))
        def newInvoiceAsJson = jsonService.convertToJson(newInvoice)
        when:
        def oldInvoice = mockMvc
                .perform(
                        put("/invoices/update/" + invoiceNumber)
                                .content(newInvoiceAsJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .response
                .contentAsString

        def updatedInvoice = mockMvc
                .perform(get("/invoices/get/" + invoiceNumber))
                .andExpect(status().isAccepted())
                .andReturn()
                .response
                .contentAsString
        def updatedInvoiceInJson = jsonService.convertToInvoices("[" + updatedInvoice + "]").get(0)

        then:
        updatedInvoiceInJson == newInvoice
    }

    def "delete Invoice"() {
        given:
        def invoiceNumber = "get invoice number"()
        def newInvoice = "Invoice to test"()
        newInvoice.setId(invoiceNumber)
        newInvoice.setDate(LocalDate.now().minusDays(10))
//        String invoiceAsJson = jsonService.convertToJson

        when:
        def response = mockMvc.perform(delete("/invoices/delete/" + invoiceNumber))
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
