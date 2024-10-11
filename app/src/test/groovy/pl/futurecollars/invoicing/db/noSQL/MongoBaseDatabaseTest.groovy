package pl.futurecollars.invoicing.db.noSQL


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.dbAbstractTest

@SpringBootTest
@IfProfileValue(name="spring.profiles.active",value = "mongo")
class MongoBaseDatabaseTest extends dbAbstractTest {

    @Autowired
    MongoBaseDatabase mongoBaseDatabase

    @Override
    Database getDataBaseInstance() {
        assert mongoBaseDatabase!=null
        return  mongoBaseDatabase
    }
}