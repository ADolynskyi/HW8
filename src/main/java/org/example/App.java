package org.example;


import org.apache.log4j.Logger;
import org.example.data.OsbbCrud;
import org.example.data.Resident;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

public class App {
    private static final Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) {

        logger.info("Start of the program");

        try (OsbbCrud crud = new OsbbCrud()
                .init()) {
            final StringBuilder sb = new StringBuilder();

            for (Resident resident : crud.getResidentsWithCarNotAllowed()) {
                sb.append(resident.getResidentId())
                        .append(" , ")
                        .append(resident.getFirstName())
                        .append(" , ")
                        .append(resident.getFamilyName())
                        .append(" , ")
                        .append(resident.getEmail())
                        .append(" , ")
                        .append(resident.getApartmentId())
                        .append(" , ")
                        .append(resident.getArea())
                        .append(" , ")
                        .append(resident.getBuildingName())
                        .append(" , ")
                        .append(resident.getStreet())
                        .append(" , ")
                        .append(resident.getBuildingNumber())
                        .append("\r\n");
            }
            System.out.println(sb);
            writeToFile(sb.toString());

        } catch (SQLException | IOException e) {
            logger.fatal(e);
        }

    }

    public static void writeToFile(String message) {
        File file = new File("Residents without permission for car.txt");
        //try-with-resources
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
