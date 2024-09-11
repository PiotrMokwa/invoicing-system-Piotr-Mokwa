package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.TestHelpers
import spock.lang.Subject

class JsonServiceTest extends TestHelpers {

    @Subject
    JsonService jsonService = new JsonService()

    def "companyInJson"() {

        return ("{" + System.lineSeparator()
                + "  \"id\" : \"1111\"," + System.lineSeparator()
                + "  \"taxIdentyfication\" : \"444-444-44-44\"," + System.lineSeparator()
                + "  \"address\" : \"Warszawa, street Marynarska\"," + System.lineSeparator()
                + "  \"insurance\" : {" + System.lineSeparator()
                + "    \"healthInsuranceBaseValue\" : 3554.89," + System.lineSeparator()
                + "    \"pensionInsurance\" : 514.57," + System.lineSeparator()
                + "    \"amountOfHealthInsurance\" : 319.94," + System.lineSeparator()
                + "    \"amountOfHealthInsuranceToReduceTax\" : 275.50" + System.lineSeparator()
                + "  }" + System.lineSeparator()
                + "}, ")
    }

    def "test ConvertToJson"() {
        when:
        def result = jsonService.convertToJson(createFirstCompany())
        then:
        result == "companyInJson"()
    }

    def "ConvertToInvoices"() {
        given:
        String listInJson = jsonService.convertToJson(listOfInvoiceToTest())
        when:
        def result = jsonService.convertToInvoices(listInJson)
        then:
        result == listOfInvoiceToTest()
    }

    def "ConvertToCompany"() {
        given:
        def listInJson = "[" + "companyInJson"().substring(0, "companyInJson"().size() - 2) + "]"
        when:
        def result = jsonService.convertToCompany(listInJson)
        then:
        result.get(0) == createFirstCompany()
    }
}
