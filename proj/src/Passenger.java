import java.util.*;
import java.util.Date;
import java.sql.*;
import java.text.*;
// import java.lang.*;

public class Passenger {
    private static Scanner sc = new Scanner(System.in);
    private static Connection con = Main.connect();
    
    public static void start(){
        System.out.println("Passenger, what would you like to do?");
        System.out.println("1. Request a ride");
        System.out.println("2. Check trip records");
        System.out.println("3. Go back");
        boolean validInput = true;
        do {
            System.out.println("Please enter [1-3]");
            int action = sc.nextInt();
            switch (action) {
                case 1:
                    validInput = true;
                    requestRide();
                    break;

                case 2:
                    validInput = true;
                    checkRecords();
                    break;

                case 3:
                    validInput = true;
                    break;

                default:
                    System.out.println("[ERROR] Invalid input.");
                    validInput = false;
            }
        }while(!validInput);
    }

    private static void requestRide(){
        int pid, p_num, year = 0;
        String start, destination, model;
        boolean validInput = true;
        do {
            do {
                System.out.println("Please enter your ID.");
                pid = sc.nextInt(); //Enter passenger id
                try {
                    Statement stmt = con.createStatement();
                    String query = "SELECT COUNT(*) FROM Passengers P WHERE P.id = " + pid;
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        validInput = false;
                        System.out.println("[ERROR] ID not found.");
                    } else {
                        validInput = true;
                    }
                } catch(SQLException se) {
                    se.printStackTrace();
                }
            // Detect for error of pid.
            } while (!validInput);
            
            do {
                System.out.println("Please enter the number of Passengers.");
                p_num = sc.nextInt();
                if ((p_num > 8) || (p_num < 1)) {
                    validInput = false;
                    System.out.println("[ERROR] Invalid number of passengers.");
                } else {
                    validInput = true;
                }
            // Detect error
            } while (!validInput);

            do {
                System.out.println("Please enter the start location.");
                start = sc.nextLine().strip();
                try {
                    Statement stmt = con.createStatement();
                    String query = "SELECT COUNT(*) FROM Taxi_stops TS WHERE TS.name = '" + start + "'";
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        validInput = false;
                        System.out.println("[ERROR] Start location not found.");
                    } else {
                        validInput = true;
                    }
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            //detect error
            } while (!validInput);

            do {
                System.out.println("Please enter the destination.");
                destination = sc.nextLine().strip();
                if (destination.equalsIgnoreCase(start)) {
                    validInput = false;
                    System.out.println("[ERROR] Destination and start location should be different.");
                    continue;
                }
                try {
                    Statement stmt = con.createStatement();
                    String query = "SELECT COUNT(*) FROM Taxi_stops TS WHERE TS.name = '" + destination + "'";
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        validInput = false;
                        System.out.println("[ERROR] Destination not found.");
                    } else {
                        validInput = true;
                    }
                } catch(SQLException se) {
                    se.printStackTrace();
                }
            //detect error
            } while (!validInput);

            System.out.println("Please enter the model. (Press enter to skip)");
            model = sc.nextLine().strip();
            //detect error
            System.out.println("Please enter the minimum driving years of the driver. (Press enter to skip)");
            year = sc.nextInt();
            //detect error
        } while(searchDriversSQL(pid, p_num, start, destination, model, year));
    }

    private static void checkRecords(){
        int pid;
        String start_date, end_date, destination;
        boolean validInput = true;
        // Scanner sc = new Scanner(System.in);
        do {
            System.out.println("Please enter your ID.");
            pid = sc.nextInt(); //Enter passenger id
            try {
                Statement stmt = con.createStatement();
                String query = "SELECT COUNT(*) FROM Passengers P WHERE P.id = " + pid;
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                if (rs.getInt(1) == 0) {
                    validInput = false;
                    System.out.println("[ERROR] ID not found.");
                } else {
                    validInput = true;
                }
            } catch(SQLException se) {
                se.printStackTrace();
            }
        // Detect for error of pid.
        } while (!validInput);

        System.out.println("Please enter the start date.");
        start_date = sc.nextLine();
        start_date = sc.nextLine();

        //detect error
        System.out.println("Please enter the end date.");
        end_date = sc.nextLine();
        //detect error
        
        do {
            System.out.println("Please enter the destination.");
            destination = sc.nextLine().strip();
            try {
                Statement stmt = con.createStatement();
                String query = "SELECT COUNT(*) FROM Taxi_stops TS WHERE TS.name = '" + destination + "'";
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                if (rs.getInt(1) == 0) {
                    validInput = false;
                    System.out.println("[ERROR] Destination not found.");
                } else {
                    validInput = true;
                }
            } catch(SQLException se) {
                se.printStackTrace();
            }
        //detect error
        } while (!validInput);
        searchRecordsSQL(pid, start_date, end_date, destination);
    }

    private static boolean searchDriversSQL(int pid, int p_num, String start, String destination, String model, int year){
        boolean search_fail = false;
        try {
            String sqlDriverInfo = "SELECT COUNT(*) FROM Drivers D, Vehicles V WHERE V.seats >= %d AND V.id = D.vehicle_id";
            if (model != null) {
                sqlDriverInfo += " AND LOCATE(V.model,\'%s\') != 0";
            }
            if (year > 0) {
                sqlDriverInfo += " AND D.driving_years >= %d ";
            }
            Statement stmt = con.createStatement();
            ResultSet driverInfo_rs = stmt.executeQuery(sqlDriverInfo);
            driverInfo_rs.next();
            int driverInfo = driverInfo_rs.getInt(1);
            // if ((model == null) && year != 0){ 
            //     //minimum driving years is required.
            //     String sqlDriverInfo = "SELECT COUNT(*) FROM Drivers D, Vehicles V WHERE V.seats >= %d AND D.driving_years >= %d AND V.id = D.vehicle_id";
            //     sqlDriverInfo = String.format(sqlDriverInfo, p_number, year);
            //     Statement stmt = con.createStatement();
            //     driverInfo = stmt.executeQuery(sqlDriverInfo);
            // } else if ((model != null) && year == 0){  
            //     //model is required.
            //     String sqlDriverInfo = "SELECT COUNT(*) FROM Drivers D, Vehicles V WHERE V.seats >= %d AND LOCATE(V.model,\'%s\') != 0 AND V.id = D.vehicle_id";
            //     sqlDriverInfo = String.format(sqlDriverInfo, p_number, model);
            //     Statement stmt = con.createStatement();
            //     driverInfo = stmt.executeQuery(sqlDriverInfo);
            // } else if ((model != null) && year != 0){ 
            //     //both model and minimum driving years are required.
            //     String sqlDriverInfo = "SELECT COUNT(*) FROM driver D, vehicle V WHERE V.seats >= %d AND LOCATE(V.model,\'%s\')!=0 AND D.driving_years>=%d AND V.id = D.vehicle_id ";
            //     sqlDriverInfo = String.format(sqlDriverInfo, p_number, model, year);
            //     Statement stmt = con.createStatement();
            //     driverInfo = stmt.executeQuery(sqlDriverInfo);
            // } else {
            //     //both model and minimum driving years are null.
            //     String sqlDriverInfo = "SELECT COUNT(*) FROM driver D, vehicle V WHERE V.seats >= %d AND V.id = D.vehicle_id ";
            //     sqlDriverInfo = String.format(sqlDriverInfo, p_number);
            //     Statement stmt = con.createStatement();
            //     driverInfo = stmt.executeQuery(sqlDriverInfo);
            // }
            if (driverInfo > 0) {
                System.out.println("Your request is placed. " + driverInfo + " drivers are able to take the request.");
                insertRequest(pid, start, destination, model, p_num, year);
                search_fail = false;
            } else {
                System.out.println("Please adjust the criteria.");
                search_fail = true;
            }
            driverInfo_rs.close();
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return search_fail;
    }

    private static void searchRecordsSQL(int pid, String start_date, String end_date, String destination){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //HH:mm:ss
        try {
            Date s_date = formatter.parse(start_date);
            Date e_date = formatter.parse(end_date);
            String recordSQL = "SELECT T.id, D.name, V.id, V.model, T.start_time, T.end_time, T.fee, T.start_location, T.destination "+
            "FROM Trips.T, Passengers P, Drivers D, Vehicles V WHERE " + 
            "WHERE T.passenger_id = %d AND T.driver_id = D.id AND D.vehicle_id = V.id AND T.destination = \'%s\' " +
            "ORDER BY T.start_time DESC";
            // AND (cast(T.start_time AS Date) >= \'%s\') AND (cast(T.end_time AS Date) <= \'%s\')
            recordSQL = String.format(recordSQL, pid, destination);
            // start_date, end_date
            Statement stmt = con.createStatement();
            ResultSet recordInfo_rs = stmt.executeQuery(recordSQL);
            System.out.println("Trip_id, Driver Name, Vehicle ID, Vehicle Model, Start, End, Fee, Start Location, Destination");
            while (recordInfo_rs.next()){
                Timestamp startTime = recordInfo_rs.getTimestamp(5);
                Timestamp endTime = recordInfo_rs.getTimestamp(6);
                if ((startTime.compareTo(s_date) >=0) && (endTime.compareTo(e_date) <= 0)){
                    int tid = recordInfo_rs.getInt(1);
                    String d_name = recordInfo_rs.getString(2);
                    int vid = recordInfo_rs.getInt(3);
                    String v_model = recordInfo_rs.getString(4);
                    String start = recordInfo_rs.getString(5);
                    String end = recordInfo_rs.getString(6);
                    int fee = recordInfo_rs.getInt(7);
                    String start_location = recordInfo_rs.getString(8);
                    String end_location = recordInfo_rs.getString(9);
                    System.out.println(tid + ", " + d_name + ", " + vid + ", " + v_model + ", " + start + ", " + end + ", " + fee + ", " + start_location + ", " + end_location);
                }
            }
            recordInfo_rs.close();
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertRequest(int pid, String start, String destination, String model, int p_num, int year){
        try {
            String insertSQL = "INSERT INTO Requests (passenger_id, start_location, destination, model, passengers, driving_years) VALUES (%d, \'%s\', \'%s\', \'%s\', %d, %d)";
            insertSQL = String.format(insertSQL, pid, start, destination, model, p_num, year);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(insertSQL);
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
