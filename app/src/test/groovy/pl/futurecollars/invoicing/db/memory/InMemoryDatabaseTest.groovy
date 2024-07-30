package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification
import spock.lang.Title

@Title("testing InMemoryDataBase")
class InMemoryDatabaseTest extends Specification{

    def " test save invoice"() {
        given:
        InMemoryDatabase inMemoryDatabase = new InMemoryDatabase(0)
        Invoice invoice = new Invoice()

        when:
       boolean result = inMemoryDatabase.save(invoice)
then:
result
        &
        inMemoryDatabase.getById(0) == Optional.of(invoice)

    }
    def " get by ID"() {
        given:
        InMemoryDatabase inMemoryDatabase = new InMemoryDatabase(0)
        Invoice invoice = new Invoice()
        inMemoryDatabase.save(invoice)
        when:
        Optional<Invoice> result = inMemoryDatabase.getById(0)
        then:
        result == Optional.of(invoice)


    }
    def " get all "() {
        given:
        InMemoryDatabase inMemoryDatabase = new InMemoryDatabase(0)
        Invoice invoice = new Invoice()
        inMemoryDatabase.save(invoice)
        when:
        List<Invoice> result = inMemoryDatabase.getAll()
        then:
        result == List.of(invoice)
    }
    def " update "() {
        given:
        InMemoryDatabase inMemoryDatabase = new InMemoryDatabase(0)
        Invoice invoice = new Invoice()
        Invoice invoiceUpdate = new Invoice()
        inMemoryDatabase.save(invoice)
        when:
        inMemoryDatabase.update(0,invoiceUpdate)
        then:
        Optional.of(invoiceUpdate) == inMemoryDatabase.getById(0)
    }

    def " delete "() {
        given:
        InMemoryDatabase inMemoryDatabase = new InMemoryDatabase(0)
        Invoice invoice = new Invoice()
        inMemoryDatabase.save(invoice)
        when:
        boolean wasDeleted = inMemoryDatabase.delete(0)
        then:
        wasDeleted
    }

}
