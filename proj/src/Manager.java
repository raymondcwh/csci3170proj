import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;


public class Manager{
    private static Scanner scanner = new Scanner(System.in);
    private static boolean validInput = true;
    private static Connection con = Main.connect();

    public void initMessage(){
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
                    break;
                default:
                    System.out.println("[ERROR] Invalid input");
                    validInput = false;
            }
        } while (!validInput);
    }

    public static void findtrips(){
        Scanner input = new Scanner(System.in);
   
        System.out.print("Please enter the minimum travelling distance.");
        int min = input.nextInt();
        while (min<0){
            System.out.print("[ERROR] Invalid travelling distance");
            min = input.nextInt();
        }
        System.out.print("Please enter the maximum travelling distance.");
        int max = input.nextInt();
        while (max<min){
            System.out.print("[ERROR] Invalid travelling distance");
            max = input.nextInt();
        }
        input.close();
        findtripsSql (min, max);
    }

public static void findtripsSql (int min,int max){
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    try {
        String sqlManagerInfo = "SELECT T.ID, D.Name, P.Name, T.Start_Location, T.Destination, Duration = T.End_Time - T.Start_Time" + 
                                "FROM Trips T, Drivers D, Passengers P" +
                                "WHERE T.DriverID = D.ID AND T.PassengerID = P.ID AND T.ID IN" + 
                                    "(SELECT T.ID" +
                                    "FROM Trips T, Taxi_stops TS1, Taxi_stops TS2" +
                                    "WHERE T.Start_Location = TS1.Name AND Destination = TS2.Name AND (abs(TS1.x-TS2.x) + abs(TS1.y-TS2.y)) >= %d AND (abs(TS1.x-TS2.x) + abs(TS1.y-TS2.y)) <= %d)";
        sqlManagerInfo = String.format(sqlManagerInfo,min,max);
        Statement stmt = con.createStatement();
        ResultSet ManagerInfo = stmt.executeQuery(sqlManagerInfo);
        System.out.println("trip id, driver name, passenger name, start location, destination, duration");
        while(ManagerInfo.next()){
            int tripId = ManagerInfo.getInt(1);
            String driverName = ManagerInfo.getString(2);
            String passengerName = ManagerInfo.getString(3);
            String startLocation = ManagerInfo.getString(4);
            String destination = ManagerInfo.getString(5);
            long duration = ManagerInfo.getInt(6);
            int diffMin = (int) (duration/(60 * 1000));
            System.out.println( tripId + ", " + driverName + ", " + passengerName + ", " + startLocation + ", " + destination + ", " + diffMin);
        }
        ManagerInfo.close();
    }catch(SQLException se){
        se.printStackTrace();
    }catch(Exception e) {
        e.printStackTrace();
    }
}
}

