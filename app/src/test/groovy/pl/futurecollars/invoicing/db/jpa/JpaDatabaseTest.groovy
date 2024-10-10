package pl.futurecollars.invoicing.db.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.IfProfileValue
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.dbAbstractTest
import pl.futurecollars.invoicing.db.jpa.InvoiceRepository
import pl.futurecollars.invoicing.db.jpa.JpaDatabase


@DataJpaTest
@IfProfileValue(name="spring.profiles.active", value ='jpa')
class JpaDatabaseTest extends dbAbstractTest {

    @Autowired
    private InvoiceRepository invoiceRepository


    @Override
    Database getDataBaseInstance() {
        assert  invoiceRepository !=null
        return new JpaDatabase(invoiceRepository)
    }
}
