package admin;

import java.sql.*;
import java.util.Scanner;
import java.io.*;

public class Admin {
    public static Connection con = connect();
    public static Scanner myObj = new Scanner(System.in);
    public static void main(String[] args) {
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
                //
                break;
            default:
                System.out.println("[ERROR] Invalid input");
        }
    }

    public static Connection connect() {
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
