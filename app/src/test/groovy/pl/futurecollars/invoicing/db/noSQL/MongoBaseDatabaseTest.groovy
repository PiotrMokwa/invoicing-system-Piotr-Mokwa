package pl.futurecollars.invoicing.db.nosql


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.dbAbstractTest
import pl.futurecollars.invoicing.model.Invoice

@ActiveProfiles("mongo")
@SpringBootTest

//@IfProfileValue(name="spring.profiles.active",value = "mongo")
class MongoBaseDatabaseTest extends dbAbstractTest {

    @Autowired
    Database<Invoice> mongoBaseDatabase

    @Override
    Database getDataBaseInstance() {
        assert mongoBaseDatabase!=null
        return  mongoBaseDatabase
    }
}