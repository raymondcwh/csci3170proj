import java.util.*;
import java.sql.*;
// import java.text.SimpleDateFormat;


public class Manager{
    private static Scanner scanner = new Scanner(System.in);
    private static boolean validInput = true;
    private static Connection con = Main.connect();

    public static void initMessage () {
        System.out.println("Manager, what would you like to do?");
        System.out.println("1. Find Trips");
        System.out.println("2. Go Back");
        do {
            System.out.println("Please enter [1-2]");
            // Scanner scanner = new Scanner(System.in);
            int action = scanner.nextInt();
            switch(action){
                case 1:
                    validInput = true;
                    findtrips();
                    break;
                case 2:
                    validInput = true;
                    break;
                default:
                    validInput = false;
                    System.out.println("[ERROR] Invalid input");
            }
        } while (!validInput);
    }

    private static void findtrips() {
        int min, max;
        do {
            System.out.print("Please enter the minimum travelling distance.");
            min = scanner.nextInt();
            if (min < 0) {
                validInput = false;
                System.out.print("[ERROR] Invalid minimum travelling distance");
            } else {
                validInput = true;
            }
        } while (!validInput);

        do {
            System.out.print("Please enter the maximum travelling distance.");
            max = scanner.nextInt();
            if (max < min){
                validInput = false;
                System.out.print("[ERROR] Invalid maximum travelling distance");
            } else {
                validInput = true;
            }
        } while (!validInput);
        // scanner.close();
        findtripsSql (min, max);
    }

    private static void findtripsSql (int min,int max) {
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            String sqlManagerInfo = "SELECT T.id, D.name, P.name, T.start_location, T.destination, duration = TIMESTAMPDIFF(MINUTE, T.start_time, T.end_time) " + 
                                    "FROM Trips T, Drivers D, Passengers P " +
                                    "WHERE T.driver_id = D.id AND T.passenger_id = P.id AND T.id IN " + 
                                    "(SELECT T.id " +
                                    "FROM Trips T, Taxi_stops TS1, Taxi_stops TS2 " +
                                    "WHERE T.start_location = TS1.name AND T.destination = TS2.name AND (abs(TS1.location_x - TS2.loaction_x) + abs(TS1.location_y - TS2.location_y)) >= %d AND (abs(TS1.location_x - TS2.location_x) + abs(TS1.location_y - TS2.location_y)) <= %d)";
            //T.end_time - T.start_time
            sqlManagerInfo = String.format(sqlManagerInfo,min,max);
            Statement stmt = con.createStatement();
            ResultSet ManagerInfo = stmt.executeQuery(sqlManagerInfo);
            System.out.println("trip id, driver name, passenger name, start location, destination, duration");
            while (ManagerInfo.next()) {
                int tripId = ManagerInfo.getInt(1);
                String driverName = ManagerInfo.getString(2);
                String passengerName = ManagerInfo.getString(3);
                String startLocation = ManagerInfo.getString(4);
                String destination = ManagerInfo.getString(5);
                long duration = ManagerInfo.getInt(6);
                int diffMin = (int)(duration/(60 * 1000));
                System.out.println( tripId + ", " + driverName + ", " + passengerName + ", " + startLocation + ", " + destination + ", " + diffMin);
            }
            ManagerInfo.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

