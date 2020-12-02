
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
                    create_tables();
                    System.out.println("Processing...Done! Tables are created");
                    break;
                case 2:
                    delete_tables();
                    System.out.println("Processing...Done! Tables are deleted");
                    break;
                case 3:
                    load_data();
                    System.out.println("Processing...Data is loaded!");
                    break;
                case 4:
                    check_data();
                    break;
                case 5:
                    break;
                default:
                    System.out.println("[ERROR] Invalid input");
                    validInput = false;
            }
        } while (!validInput);
    }

    public static void create_tables() {

        String drivers = "CREATE TABLE IF NOT EXISTS Drivers (id integer NOT NULL,name varchar(30) NOT NULL,vehicle_id varchar(6) NOT NULL,driving_years integer NOT NULL,PRIMARY KEY (id),FOREIGN KEY (vehicle_id) REFERENCES Vehicles (id));";
        // "CREATE TABLE IF NOT EXISTS Drivers (\n"
        //         + "ID integer NOT NULL,\n"
        //         + "Name varchar(30) NOT NULL,\n"
        //         + "Vehicle_ID varchar(6) FOREIGN KEY REFERENCES Vehicles (ID) NOT NULL,\n"
        //         + "Driving_years integer NOT NULL,\n"
        //                 PRIMARY KEY(ID),\n
        //                 FOREIGN KEY(Vehicle_ID) REFERENCES Vehicles,
        //                 );"";

        String vehicles = "CREATE TABLE IF NOT EXISTS Vehicles (id varchar(6) NOT NULL,model varchar(30) NOT NULL,seats integer NOT NULL,PRIMARY KEY(id));";

        String passengers = "CREATE TABLE IF NOT EXISTS Passengers (id integer NOT NULL, name varchar(30) NOT NULL, PRIMARY KEY(id));";

        String trips = "CREATE TABLE IF NOT EXISTS Trips (\n"
                + "	id integer NOT NULL,\n"
                + "	driver_id integer NOT NULL,\n"
                + "	passenger_id integer NOT NULL,\n"
                + "	start_time datetime NOT NULL,\n"
                + "	end_time datetime NOT NULL,\n"
                + "	start_location varchar(20) NOT NULL,\n"
                + "	destination varchar(20) NOT NULL,\n"
                + "	fee integer NOT NULL,\n"
                + " PRIMARY KEY(id),\n"
                + "	FOREIGN KEY(driver_id) REFERENCES Drivers(id),\n"
                + "	FOREIGN KEY(passenger_id) REFERENCES Passengers(id)\n"
                + ");";

        String taxi_stops = "CREATE TABLE IF NOT EXISTS Taxi_stops (name varchar(20) NOT NULL, location_x integer NOT NULL,location_y integer NOT NULL,PRIMARY KEY(name));";

        String requests = "CREATE TABLE IF NOT EXISTS Requests (\n"
                + "	id integer NOT NULL,\n"
                + "	passenger_id integer NOT NULL,\n"
                + "	start_location varchar(20) NOT NULL,\n"
                + "	destination integer NOT NULL,\n"
                + "	model varchar(30) NOT NULL,\n"
                + " passengers integer NOT NULL,\n"
                + "	taken boolean NOT NULL,\n"
                + "	driving_years integer NOT NULL,\n"
                + " PRIMARY KEY(id),\n"
                + "	FOREIGN KEY(passenger_id) REFERENCES Passengers(id)\n"
                + ");";


        try {
            Statement stmt = con.createStatement();
            // create a new table
            stmt.execute(vehicles);
            stmt.execute(drivers);
            stmt.execute(passengers);
            stmt.execute(trips);
            stmt.execute(taxi_stops);
            stmt.execute(requests);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    public static void delete_tables() {
        String drivers = "DROP TABLE IF EXISTS Drivers;";
        String vehicles = "DROP TABLE IF EXISTS Vehicles;";
        String passengers = "DROP TABLE IF EXISTS Passengers;";
        String requests = "DROP TABLE IF EXISTS Requests;";
        String trips = "DROP TABLE IF EXISTS Trips;";
        String taxi_stops = "DROP TABLE IF EXISTS Taxi_stops;";

        try {
            Statement stmt = con.createStatement();
            // create a new table
            stmt.execute(trips);
            stmt.execute(requests);
            stmt.execute(drivers);
            stmt.execute(passengers);
            stmt.execute(vehicles);
            stmt.execute(taxi_stops);
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }

    public static void load_data() {
        String[] csv_files = {"drivers.csv","vehicles.csv","passengers.csv","trips.csv","taxi_stops.csv"};
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
//                                ResultSet rs = stmt.executeQuery("SELECT * FROM Drivers");
//                                while (rs.next()) {
//                                    String x = rs.getString("name");
//                                    System.out.println(x+",");
//                                }
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

    public static void check_data(){
        String[] tables = {"Drivers","Vehicles","Passengers","Requests","Trips","Taxi_stops"};
        System.out.println("Numbers of records in each table:");
        for (String table: tables) {
            try {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT (*) FROM " + table);
                int rows = rs.getInt(0);
                System.out.println(table.substring(0, table.length()-1) + ": " + rows);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
