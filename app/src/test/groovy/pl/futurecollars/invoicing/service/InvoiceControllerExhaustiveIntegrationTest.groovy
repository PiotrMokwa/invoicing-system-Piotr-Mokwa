package pl.futurecollars.invoicing.service

import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Shared

import java.nio.file.DirectoryNotEmptyException
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("prod")
@SpringBootTest
@AutoConfigureMockMvc
@EnableSharedInjection
class InvoiceControllerExhaustiveIntegrationTest extends TestHelpers {
    @Shared
    @Autowired
    private MockMvc mockMvc

    @Shared
    @Autowired
    private JsonService jsonService

    def setup() {
        "delete all invoices"(mockMvc, jsonService)
    }

    Invoice "Invoice nr1"() {
        def invoice = createFirstInvoice(createFirstCompany(), createSecondCompany())
        invoice.id = 1;
        invoice.date = LocalDate.now().minusDays(1)
        return invoice
    }

    Invoice "Invoice nr2"() {
        def invoice = createFirstInvoice(createFirstCompany(), createSecondCompany())
        invoice.id = 2;
        invoice.date = LocalDate.now().minusDays(2)
        return invoice
    }

    Invoice "Invoice nr3"() {
        def invoice = createFirstInvoice(createFirstCompany(), createSecondCompany())
        invoice.id = 3;
        invoice.date = LocalDate.now().minusDays(3)
        return invoice
    }

//    def "AddInvoiceFailureTest"() {
//
//        expect: "request without content "
//        mockMvc.perform(
//                post("/invoices/add").content(null))
//                .andExpect(status().isBadRequest())
//
//    }

    def "GetInvoicesFailureTest"() {
        given:
        System.out.println(
                mockMvc.perform(get("/invoices/GET Invoices"))
                        .andExpect(status().isNotFound()).andReturn().response.contentAsString
        )

        expect: "request when base is empty"
        mockMvc.perform(get("/invoices/GET Invoices"))
                .andExpect(status().isNotFound())


    }

    def "GetInvoiceFailureTest"() {

        expect:
        mockMvc.perform(
                get("/invoices/1"))
                .andExpect(status().isNotFound())

    }

    def "UpdateInvoiceFailureTest"() {
        given: "Create invoice, create invoice to replace, add invoice to base,"
//        System.out.println("Is file exist: " + isFileBaseExist())
//        System.out.println("next id: " + Files.readAllLines(Path.of("SpringId.txt")))
        def newInvoice = "Invoice nr2"()
        def newInvoiceInJson = jsonService.convertToJson(newInvoice)
        expect: " Update not existing invoice "
        mockMvc.perform(
                put("/invoices/update/10")
                        .content(newInvoiceInJson)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())

    }

    def "Delete invoice"() {

        when: "Delete not existing invoice "
        def result = mockMvc.perform(delete("/invoices/delete/10"))
                .andExpect(status().isNotFound())
                .andReturn()
                .response
                .contentAsString
        then: "check if response body is empty"
        result == ""

    }

    def cleanupSpec() {
        "delete all invoices"(mockMvc, jsonService)
    }

}