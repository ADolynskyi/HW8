package org.example.data;

import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import static org.example.Config.*;

public class OsbbCrud implements Closeable {
    private static final Logger logger = Logger.getLogger(OsbbCrud.class);
    private static final String SQL_RESIDENTS_WITH_CAR_NOT_ALLOWED =
            "SELECT res.resident_id ,first_name, family_name, email, ra.apartment_id,\n" +
                    "ap.area, b.name, b.street, b.number\n" +
                    "FROM residents AS res\n" +
                    "JOIN resident_apartment AS ra ON ra.resident_id=res.resident_id\n" +
                    "JOIN apartments AS ap ON ra.apartment_id=ap.apartment_id\n" +
                    "JOIN buildings AS b ON ap.building_id=b.building_id\n" +
                    "WHERE car=0 AND res.resident_id IN (\n" +
                    "\t\tSELECT resident_id\n" +
                    "\t\tFROM resident_apartment AS ra\n" +
                    "\t\tGROUP BY resident_id\n" +
                    "\t\tHAVING COUNT( ra.resident_id)<2)";
    private Connection conn = null;

    private void fwMigration() {
        logger.debug("Migrated successfully");

        Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .locations("classpath:flyway.scripts")
                .load()
                .migrate();

    }

    public OsbbCrud init() throws SQLException {
        logger.info("Crud initialized");
        fwMigration();
        conn = DriverManager.getConnection(jdbcUrl, username, password);
        return this;
    }

    @Override
    public void close() throws IOException {
        try {
            conn.close();
            conn = null;
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public List<Resident> getResidentsWithCarNotAllowed() throws SQLException {
        logger.trace("Call getting residents with out car entrance permission");
        final List<Resident> result = new LinkedList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(SQL_RESIDENTS_WITH_CAR_NOT_ALLOWED)) {
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Resident res = new Resident();
                res.setResidentId(resultSet.getInt("resident_id"));
                res.setFirstName(resultSet.getString("first_name"));
                res.setFamilyName(resultSet.getString("family_name"));
                res.setEmail(resultSet.getString("email"));
                res.setApartmentId(resultSet.getInt("apartment_id"));
                res.setArea(resultSet.getInt("area"));
                res.setBuildingName(resultSet.getString("name"));
                res.setStreet(resultSet.getString("street"));
                res.setBuildingNumber(resultSet.getInt("number"));
                result.add(res);
            }
        }
        return result;
    }

}
