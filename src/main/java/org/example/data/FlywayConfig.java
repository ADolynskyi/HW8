package org.example.data;

import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;

import static org.example.Config.*;

public class FlywayConfig {
    private static final Logger logger = Logger.getLogger(FlywayConfig.class);

    private FlywayConfig() {
    }

    public static void fwMigration() {
        logger.debug("Migrated successfully");

        Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .locations("classpath:flyway.scripts")
                .load()
                .migrate();

    }
}
