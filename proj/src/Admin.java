
import java.sql.*;
import java.util.Scanner;
import java.io.*;

public class Admin {
    private static Scanner myObj = new Scanner(System.in);
    private static Connection con = Main.connect();
    private static boolean validInput = true;
    // Admin() {

    // }

    public static void start() {
        System.out.println("Administrator, what would you like to do?");
        System.out.println("1. Create tables");
        System.out.println("2. Delete tables");
        System.out.println("3. Load data");
        System.out.println("4. Check data");
        System.out.println("5. Go back");
        do {
            System.out.println("Please enter [1-5]");
            int taskNo = myObj.nextInt();
            switch (taskNo) {
                case 1:
                    validInput = true;
                    create_tables();
                    System.out.println("Processing...Done! Tables are created");
                    break;
                case 2:
                    validInput = true;
                    delete_tables();
                    System.out.println("Processing...Done! Tables are deleted");
                    break;
                case 3:
                    validInput = true;
                    load_data();
                    System.out.println("Processing...Data is loaded!");
                    break;
                case 4:
                    validInput = true;
                    check_data();
                    break;
                case 5:
                    validInput = true;
                    break;
                default:
                    validInput = false;
                    System.out.println("[ERROR] Invalid input");
            }
        } while (!validInput);
    }

    private static void create_tables() {

        String drivers = "CREATE TABLE IF NOT EXISTS Drivers (" 
                + " id integer NOT NULL AUTO_INCREMENT,"
                + " name varchar(30) NOT NULL,"
                + " vehicle_id varchar(6) NOT NULL,"
                + " driving_years integer NOT NULL,"
                + " PRIMARY KEY (id),"
                + " FOREIGN KEY (vehicle_id) REFERENCES Vehicles (id)" 
                + ")";

        String vehicles = "CREATE TABLE IF NOT EXISTS Vehicles ("
                + " id varchar(6) NOT NULL AUTO_INCREMENT,"
                + " model varchar(30) NOT NULL,"
                + " seats integer NOT NULL,"
                + " PRIMARY KEY(id)"
                + ")";

        String passengers = "CREATE TABLE IF NOT EXISTS Passengers ("
                + " id integer NOT NULL AUTO_INCREMENT,"
                + " name varchar(30) NOT NULL,"
                + " PRIMARY KEY(id)"
                + ")";

        String trips = "CREATE TABLE IF NOT EXISTS Trips ("
                + "	id integer NOT NULL AUTO_INCREMENT,"
                + "	driver_id integer NOT NULL,"
                + "	passenger_id integer NOT NULL,"
                + "	start_time datetime NOT NULL,"
                + "	end_time datetime,"
                + "	start_location varchar(20) NOT NULL,"
                + "	destination varchar(20) NOT NULL,"
                + "	fee integer unsigned NOT NULL DEFAULT 0,"
                + " PRIMARY KEY(id),"
                + "	FOREIGN KEY(driver_id) REFERENCES Drivers(id),"
                + "	FOREIGN KEY(passenger_id) REFERENCES Passengers(id)"
                + ")";

        String taxi_stops = "CREATE TABLE IF NOT EXISTS Taxi_stops ("
                + " name varchar(20) NOT NULL AUTO_INCREMENT,"
                + " location_x integer NOT NULL,"
                + " location_y integer NOT NULL,"
                + " PRIMARY KEY(name)"
                + ")";

        String requests = "CREATE TABLE IF NOT EXISTS Requests (\n"
                + "	id integer NOT NULL AUTO_INCREMENT,\n"
                + "	passenger_id integer NOT NULL,\n"
                + "	start_location varchar(20) NOT NULL,\n"
                + "	destination varchar(20) NOT NULL,\n"
                + "	model varchar(30),\n"
                + " passengers integer NOT NULL,\n"
                + "	taken boolean DEFAULT false,\n"
                + "	driving_years integer unsigned DEFAULT 0,\n"
                + " PRIMARY KEY(id),\n"
                + "	FOREIGN KEY(passenger_id) REFERENCES Passengers(id)\n"
                + ")";
        
                try {
            Statement stmt = con.createStatement();
            // create a new table
            stmt.executeUpdate(vehicles);
            stmt.executeUpdate(drivers);
            stmt.executeUpdate(passengers);
            stmt.executeUpdate(trips);
            stmt.executeUpdate(taxi_stops);
            stmt.executeUpdate(requests);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void delete_tables() {
        String drivers = "DROP TABLE IF EXISTS Drivers";
        String vehicles = "DROP TABLE IF EXISTS Vehicles";
        String passengers = "DROP TABLE IF EXISTS Passengers";
        String requests = "DROP TABLE IF EXISTS Requests";
        String trips = "DROP TABLE IF EXISTS Trips";
        String taxi_stops = "DROP TABLE IF EXISTS Taxi_stops";

        try {
            Statement stmt = con.createStatement();
            // create a new table
            stmt.executeUpdate(trips);
            stmt.executeUpdate(requests);
            stmt.executeUpdate(drivers);
            stmt.executeUpdate(passengers);
            stmt.executeUpdate(vehicles);
            stmt.executeUpdate(taxi_stops);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void load_data() {
        String[] csv_files = {"vehicles.csv","drivers.csv","passengers.csv","trips.csv","taxi_stops.csv"};
        System.out.println("Please enter the folder path");
        String folder_path = myObj.next();
        for (String csv_file : csv_files) {
            try {
                File df = new File(folder_path + "/" + csv_file);
                Scanner myReader = new Scanner(df);
                while (myReader.hasNextLine()) {
                    String data_line = myReader.nextLine();
                    String[] data = data_line.split(",");
                    switch (csv_file) {
                        case "drivers.csv":
                            try {
                                Statement stmt = con.createStatement();
                                data[1] = "'" + data[1] + "'";
                                data[2] = "'" + data[2] + "'";
                                stmt.executeUpdate("INSERT into Drivers VALUES (" + String.join(",",data) + ")");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "vehicles.csv":
                            try {
                                Statement stmt = con.createStatement();
                                data[0] = "'" + data[0] + "'";
                                data[1] = "'" + data[1] + "'";
                                stmt.executeUpdate("INSERT into Vehicles VALUES (" + String.join(",",data) + ")");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "passengers.csv":
                            try {
                                Statement stmt = con.createStatement();
                                data[1] = "'" + data[1] + "'";
                                stmt.executeUpdate("INSERT into Passengers VALUES (" + String.join(",",data) + ")");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "trips.csv":
                            try {
                                Statement stmt = con.createStatement();
                                data[3] = "'" + data[3] + "'";
                                data[4] = "'" + data[4] + "'";
                                data[5] = "'" + data[5] + "'";
                                data[6] = "'" + data[6] + "'";
                                stmt.executeUpdate("INSERT into Trips VALUES (" + String.join(",",data) + ")");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "taxi_stops.csv":
                            try {
                                Statement stmt = con.createStatement();
                                data[0] = "'" + data[0] + "'";
                                stmt.executeUpdate("INSERT into Taxi_stop VALUES (" + String.join(",",data) + ")");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                    }
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }

    private static void check_data(){
        String[] tables = {"Drivers","Vehicles","Passengers","Requests","Trips","Taxi_stops"};
        System.out.println("Numbers of records in each table:");
        for (String table: tables) {
            try {
                Statement stmt = con.createStatement();
                String query = "SELECT COUNT(*) FROM " + table;
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                int rows = rs.getInt(1);
                System.out.println(table.substring(0, table.length()-1) + ": " + rows);
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
