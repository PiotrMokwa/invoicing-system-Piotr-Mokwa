package pl.futurecollars.invoicing.db.sql

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.dbAbstractTest
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import spock.lang.Specification

import javax.sql.DataSource

class SqlDatabaseTest extends dbAbstractTest {

    @Override
     Database getDataBaseInstance() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource)

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration")
                .load()
        flyway.clean()
        flyway.migrate()

        def database = new SqlDatabase(jdbcTemplate)


        return database
    }

}


