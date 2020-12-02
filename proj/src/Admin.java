import java.sql.*;
import java.util.Scanner;
import java.io.*;

public class Admin {
    private static Connection con = connect();
    private static Scanner myObj = new Scanner(System.in);

    // Admin() {

    // }

    public static void start() {
        System.out.println("Administrator, what would you like to do?");
        System.out.println("1. Create tables");
        System.out.println("2. Delete tables");
        System.out.println("3. Load data");
        System.out.println("4. Check data");
        System.out.println("5. Go back");
        System.out.println("Please enter [1-5]");
        int taskNo = myObj.nextInt();
        switch (taskNo) {
            case 1:
                //
                break;
            case 2:
                //
                break;
            case 3:
                load_data();
                break;
            case 4:
                check_data();
                break;
            case 5:
                
                break;
            default:
                System.out.println("[ERROR] Invalid input");
        }
    }

    private static Connection connect() {
        String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group45";
        String dbUsername = "Group45";
        String dbPassword = "3170group45";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR]: Java MYSQL DB Driver not found!");
            System.exit(0);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return con;
    }

    public static void create_tables() {

        String sql1 = "CREATE TABLE IF NOT EXISTS Drivers (\n"
            + "	ID integer NOT NULL,\n"
            + "	Name varchar(30) NOT NULL,\n"
            + "	Vehicle_ID varchar(6) FOREIGN KEY REFERENCES Vehicles (ID) NOT NULL,\n"
            + " Driving_years integer NOT NULL,\n"
            + " PRIMARY KEY(ID),\n"
            + "	FOREIGN KEY(Vehicle_ID) REFERENCES Vehicles,\n"
            + ");";

        String sql2 = "CREATE TABLE IF NOT EXISTS Vehicles (\n"
            + "	ID varchar(6) NOT NULL,\n"
            + "	Model varchar(30) NOT NULL,\n"
            + "	Seats integer NOT NULL,\n"
            + " PRIMARY KEY(ID),\n"
            + ");";

        String sql3 = "CREATE TABLE IF NOT EXISTS Passengers (\n"
            + "	ID integer NOT NULL,\n"
            + "	Name varchar(30) NOT NULL,\n"
            + " PRIMARY KEY(ID),\n"
            + ");";

        String sql4 = "CREATE TABLE IF NOT EXISTS Trips (\n"
            + "	ID integer PRIMARY KEY NOT NULL,\n"
            + "	Driver_ID integer NOT NULL,\n"
            + "	Passenger_ID integer NOT NULL,\n"
            + "	Start_time datetime NOT NULL,\n"
            + "	End_time datetime NOT NULL,\n"
            + "	Start_location varchar(20) NOT NULL,\n"
            + "	Destination varchar(20) NOT NULL,\n"
            + "	Fee Integer NOT NULL,\n"
            + " PRIMARY KEY(ID),\n"
            + "	FOREIGN KEY(Driver_ID) REFERENCES Driver,\n"
            + "	FOREIGN KEY(Passenger_ID) REFERENCES Passenger,\n"
            + "	FOREIGN KEY(Vehicle_ID) REFERENCES Vehicles,\n"
            + ");";

        String sql5 = "CREATE TABLE IF NOT EXISTS Taxi_stops (\n"
            + "	Name varchar(20) NOT NULL,\n"
            + "	Location_x integer NOT NULL,\n"
            + "	Location_y integer NOT NULL,\n"
            + " PRIMARY KEY(Name),\n"
            + ");";

        String sql6 = "CREATE TABLE IF NOT EXISTS Request (\n"
            + "	ID integer NOT NULL,\n"
            + "	Passenger_ID integer NOT NULL,\n"
            + "	Start_location varchar(20) NOT NULL,\n"
            + "	destination integer NOT NULL,\n"
            + "	model varchar(30) NOT NULL,\n"
            + " passengers integer NOT NULL,\n"
            + "	taken boolean NOT NULL,\n"
            + "	driving_years integer NOT NULL,\n"
            + " PRIMARY KEY(ID),\n"
            + "	FOREIGN KEY(Passenger_ID) REFERENCES Passenger,\n"
            + ");";


        try (Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql2);
            stmt.execute(sql1);
            stmt.execute(sql3);
            stmt.execute(sql4);
            stmt.execute(sql5);
            stmt.execute(sql6);

            System.out.println("Processing...Done! Tables are created");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }



    }

    public static void delete_tables() {

        String sql11 = "DROP TABLE Driver";
        String sql12 = "DROP TABLE Vehicle";
        String sql13 = "DROP TABLE Passenger";
        String sql14 = "DROP TABLE Request";
        String sql15 = "DROP TABLE Trip";
        String sql16 = "DROP TABLE Taxi_stops";

        try (Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql11);
            stmt.execute(sql12);
            stmt.execute(sql13);
            stmt.execute(sql14);
            stmt.execute(sql15);
            stmt.execute(sql16);

            System.out.println("Processing...Done! Tables are deleted");

        } catch (SQLException e) {
            System.out.println(e.getMessage());

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
                                stmt.executeUpdate("INSERT into Trips VALUES (" + String.join(",",data) + ")");
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
        System.out.println("Processing...Data is loaded!");
    }

    public static void check_data(){
        String[] tables = {"Drivers","Vehicles","Passengers","Requests","Trips","Taxi_Stops"};
        System.out.println("Numbers of records in each table:");
        for (String table: tables) {
            try {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT (*) from " + table);
                int rows = rs.getInt(0);
                System.out.println(table.substring(0, table.length()-1) + ": " + rows);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
