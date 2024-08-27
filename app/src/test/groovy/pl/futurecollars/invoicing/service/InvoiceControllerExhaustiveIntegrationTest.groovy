package pl.futurecollars.invoicing.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice

import java.nio.file.DirectoryNotEmptyException
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class InvoiceControllerExhaustiveIntegrationTest extends TestHelpers {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService
    int index = 0

    def setup() {

//fileBase
        deleteFilesBase(baseTestFileSpring,baseIdTestFileSpring)
        createEmptyFilesBase(baseTestFileSpring,baseIdTestFileSpring)

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
                put("/invoices/update/5")
                        .content(newInvoiceInJson)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())

    }

    def "Delete invoice"() {

        when: "Delete not existing invoice "


        def result = mockMvc.perform(delete("/invoices/delete/2"))
                .andExpect(status().isNotFound())
                .andReturn()
                .response
                .contentAsString
        then:
        result == "Invoice was not deleted"
    }


    def cleanup() {

//        if (isFileBaseExist()) {
//            deleteFilesBase()
//            createEmptyFilesBase()
////            System.out.println(index++)
//        }
//
//        def base = mockMvc.perform(get("/invoices/GET Invoices"))
//                .andReturn()
//                .response
//                .contentAsString
//
//        System.out.println("Base befor delete" + base)
//        List<Invoice> baseList = jsonService.convertToInvoices(base)
//
//        if(baseList == null)
//        if (!baseList.isEmpty() ) {
//            System.out.print(baseList)
//            int baseSize = baseList.size()
//            for (int i = 0; i < baseSize; i++) {
//                String path = "/invoices/delete/" + i
//                System.out.println(path)
//                mockMvc.perform(delete(path))
//                System.out.println("delete executed nr " + i)
//            }
//
//            def baseDelete = mockMvc.perform(get("/invoices/GET Invoices"))
//                    .andReturn()
//                    .response
//                    .contentAsString
//            System.out.println("Delete base" + baseDelete)
//        }
    }

    def cleanupSpec() {


//Clean File Base
        deleteFilesBase(baseTestFileSpring,baseIdTestFileSpring)

    }
}
