import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class Driver {
    boolean quit = false;
    Connection conn = Main.connect();

    public void initMessage(){
        do {
            System.out.println("Driver, what would you like to do?");
            System.out.println("1. Search requests");
            System.out.println("2. Take a request");
            System.out.println("3. Finish a trip");
            System.out.println("4. Go back");
            System.out.println("Please enter [1-4]");
            Scanner scanner = new Scanner(System.in);
            int action = scanner.nextInt();
            while (action<1||action>4){
                System.out.println("[ERROR] Invalid input.");
                System.out.println("Please enter [1-4]");
                action = scanner.nextInt();
            }
            switch (action) {
                case 1:
                    searchRequests();
                    break;

                case 2:
                    takeRequest();
                    break;

                case 3:
                    finishTrip();
                    break;

                case 4:
                    quit = true;
                    break;
            }
        }while(!quit);
    }

    public void searchRequests(){

        System.out.println("Please enter your ID.");
        Scanner scanner = new Scanner(System.in);
        int driverId = scanner.nextInt();
        while (!validId("Drivers", "id",driverId)){
            System.out.println("[ERROR] Driver not found.");
            System.out.println("Please enter your ID.");
            driverId = scanner.nextInt();
        }
        System.out.println("Please enter the coordinates of your location.");
        int locX = scanner.nextInt();
        int locY = scanner.nextInt();
        System.out.println("Please enter the maximum distance from you to the passenger.");
        int maxDistance = scanner.nextInt();
        searchRequestsSql(driverId,locX,locY,maxDistance);
    }

    public void takeRequest(){
        System.out.println("Please enter your ID.");
        Scanner scanner = new Scanner(System.in);
        int driverId = scanner.nextInt();
        while (!validId("Drivers", "id",driverId)){
            System.out.println("[ERROR] Driver not found.");
            System.out.println("Please enter your ID.");
            driverId = scanner.nextInt();
        }
        System.out.println("Please enter the request ID.");
        int requestId = scanner.nextInt();
        while (!validId("Requests", "id",requestId)){
            System.out.println("[ERROR] Request not found.");
            System.out.println("Please enter the request ID.");
            driverId = scanner.nextInt();
        }
        takeRequestSQL(driverId,requestId);
    }

    public void finishTrip(){
        System.out.println("Please enter your ID.");
        Scanner scanner = new Scanner(System.in);
        int driverId = scanner.nextInt();
        while (!validId("Drivers", "id",driverId)){
            System.out.println("[ERROR] Driver not found.");
            System.out.println("Please enter your ID.");
            driverId = scanner.nextInt();
        }

        int trip_Id = unfinishedTripSql(driverId);
        if (trip_Id == -1){
            System.out.println("[ERROR] You do not have unfinished trips.");
            return;
        }

        scanner.nextLine();
        System.out.println("Do you wish to finish the trip? [y/n]");
        String yesNo = scanner.nextLine();
        while (!yesNo.equalsIgnoreCase("y")&&!yesNo.equalsIgnoreCase("n")){
            System.out.println("[ERROR] Invalid input.");
            System.out.println("Do you wish to finish the trip? [y/n]");
            yesNo = scanner.nextLine();
            System.out.println(yesNo);
        }
        switch (yesNo) {
            case "y":
                finishTripSql(trip_Id);
                break;

            case "n":
                break;

        }
    }

    public void searchRequestsSql (int driverId,int locX,int locY,int maxDistance){
        //get [driving_years, model, seats]
        int driving_years = -1;
        String model = "";
        int seats = -1;
        try{
            String sqlDriverInfo = "SELECT D.driving_years,V.model,V.seats FROM Drivers D, Vehicles V WHERE D.id = %d AND V.id = D.vehicle_id ";
            sqlDriverInfo = String.format(sqlDriverInfo,driverId);
            Statement stmt = conn.createStatement();
            ResultSet driverInfo = stmt.executeQuery(sqlDriverInfo);
            while(driverInfo.next()){
                driving_years = driverInfo.getInt(1);
                model = driverInfo.getString(2);
                seats = driverInfo.getInt(3);
            }
            driverInfo.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
        try {
        String sqlRequestInfo = "SELECT R.id, P.name, R.passengers, R.start_location, R.destination FROM Requests R, Passengers P, Taxi_stops T" +
                " WHERE R.Driving_years <= %d AND  R.passengers <= %d AND LOCATE(R.model,\'%s\')!=0 AND T.name = R.start_location AND R.passenger_id = P.id AND (ABS(T.location_x-%d)+ABS(T.location_y-%d)) <= %d" +
                " AND R.taken = false";
            sqlRequestInfo = String.format(sqlRequestInfo, driving_years, seats, model,locX, locY, maxDistance);
            Statement stmt = conn.createStatement();
            ResultSet requestInfo = stmt.executeQuery(sqlRequestInfo);
            System.out.println("request ID, passenger name, num of passengers, start location, destination");
            while (requestInfo.next()) {
                int RID = requestInfo.getInt(1);
                String passengerName = requestInfo.getString(2);
                int numPassenger = requestInfo.getInt(3);
                String startLoc = requestInfo.getString(4);
                String destination = requestInfo.getString(5);
                System.out.println(RID + ", " + passengerName + ", " + numPassenger + ", " + startLoc + ", " + destination);
            }
            requestInfo.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void takeRequestSQL (int driverId,int requestId){
        //Check valid
        ArrayList<Integer> validList = new ArrayList<>();
        int driving_years = -1;
        String model = "";
        int seats = -1;
        int passengerId = -1;
        String startLoc = "";
        String destination = "";
        long tripId = -1;
        boolean taken = false;

        //get [driving_years, model, seats]
        try{
            String sqlDriverInfo = "SELECT D.driving_years,V.model,V.seats FROM Drivers D, Vehicles V WHERE D.id = %d AND V.id = D.vehicle_id ";
            sqlDriverInfo = String.format(sqlDriverInfo,driverId);
            Statement stmt = conn.createStatement();
            ResultSet driverInfo = stmt.executeQuery(sqlDriverInfo);
            while(driverInfo.next()){
                driving_years = driverInfo.getInt(1);
                model = driverInfo.getString(2);
                seats = driverInfo.getInt(3);
            }
            driverInfo.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }

        try{
            String driverTaken = "SELECT T.driver_id FROM Trips T WHERE T.driver_id = %d AND T.end_time IS NULL ";
            driverTaken = String.format(driverTaken,driverId);
            Statement stmt = conn.createStatement();
            ResultSet requestInfo = stmt.executeQuery(driverTaken);
            while(requestInfo.next()){
                int DID = requestInfo.getInt(1);
                if (DID == driverId)
                    taken = true;
            }
            requestInfo.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }

        try{
            String sqlRequestInfo = "SELECT R.id FROM Requests R, Passengers P" +
                    " WHERE R.Driving_years <= %d AND  R.passengers <= %d AND LOCATE(R.model,\'%s\')!=0 AND R.passenger_id = P.id AND R.taken = false";
            sqlRequestInfo = String.format(sqlRequestInfo,driving_years,seats,model);
            Statement stmt = conn.createStatement();
            ResultSet requestInfo = stmt.executeQuery(sqlRequestInfo);
            while(requestInfo.next()){
                int RID = requestInfo.getInt(1);
                validList.add(RID);
            }
            requestInfo.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }

        //Record taken
        if(validList.contains(requestId) && taken == false) {
            try {
                String update = "UPDATE Requests SET taken = true WHERE id = %d";
                update = String.format(update, requestId);
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(update);
            }catch(SQLException se){
                se.printStackTrace();
            }catch(Exception e) {
                e.printStackTrace();
            }
        }else if(!validList.contains(requestId)){
            System.out.println("[ERROR] You do not fulfill request requirement.");
            return;
        }else if(taken){
            System.out.println("[ERROR] You are currently in a trip.");
            return;
        }

        //Create trip
        try {
            String info = "SELECT R.passenger_id, R.start_location, R.destination FROM Requests R WHERE R.id = %d";
            info = String.format(info, requestId);
            Statement stmt = conn.createStatement();
            ResultSet insertInfo = stmt.executeQuery(info);
            while (insertInfo.next()) {
                passengerId = insertInfo.getInt(1);
                startLoc = insertInfo.getString(2);
                destination = insertInfo.getString(3);
            }
            insertInfo.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            String insert = "INSERT INTO Trips (driver_id, passenger_id, start_location, destination, start_time) VALUES(%d, %d, \'%s\', \'%s\', \'%s\')";
            insert = String.format(insert, requestId, passengerId, startLoc, destination, formatter.format(timestamp));
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(insert,Statement.RETURN_GENERATED_KEYS);
            ResultSet result = stmt.getGeneratedKeys();
            if (result.next()) {
                tripId = result.getLong(1);
            }
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }

        //Display Trip
        try {
            String tripInfo = "SELECT T.id, P.name, T.start_time FROM Trips T, Passengers P WHERE T.id = %d AND T.passenger_id = P.id";
            tripInfo = String.format(tripInfo, tripId);
            Statement stmt = conn.createStatement();
            ResultSet displayTrip = stmt.executeQuery(tripInfo);
            System.out.println("Trip ID, Passenger name, Start");
            while (displayTrip.next()) {
                int Id = displayTrip.getInt(1);
                String passengerName = displayTrip.getString(2);
                Timestamp tempStartTime = displayTrip.getTimestamp(3);
                String startTime = formatter.format(tempStartTime);
                System.out.println(Id + ", " + passengerName + ", " + startTime);
            }
            displayTrip.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int unfinishedTripSql (int driverId){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int Id = -1;
        try {
            String unfinishedTrip = "SELECT T.id, T.passenger_id, T.start_time FROM Trips T WHERE T.driver_id = %d AND T.end_time is NULL";
            unfinishedTrip = String.format(unfinishedTrip, driverId);
            Statement stmt = conn.createStatement();
            ResultSet displayUnfinishedTrip = stmt.executeQuery(unfinishedTrip);
            System.out.println("Trip ID, Passenger ID, Start");
            while (displayUnfinishedTrip.next()) {
                Id = displayUnfinishedTrip.getInt(1);
                int passengerId = displayUnfinishedTrip.getInt(2);
                Timestamp tempStartTime = displayUnfinishedTrip.getTimestamp(3);
                String startTime = formatter.format(tempStartTime);
                System.out.println(Id + ", " + passengerId + ", " + startTime);
            }
            displayUnfinishedTrip.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    public void finishTripSql (int tripId){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        int diffMin = -1;
        try {
            String update = "UPDATE Trips SET end_time = \'%s\' WHERE id = %d";
            update = String.format(update, formatter.format(timestamp), tripId);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(update);
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }

        try {
            String tripInfoSQL = "SELECT T.start_time, T.end_time FROM Trips T WHERE T.id = %d";
            tripInfoSQL = String.format(tripInfoSQL, tripId);
            Statement stmt = conn.createStatement();
            ResultSet tripInfo = stmt.executeQuery(tripInfoSQL);
            System.out.println("Trip ID, Passenger ID, Start");
            while (tripInfo.next()) {
                Timestamp startTime = tripInfo.getTimestamp(1);
                Timestamp endTime = tripInfo.getTimestamp(2);
                long diff = endTime.getTime() - startTime.getTime();
                diffMin = (int) (diff/(60 * 1000));
            }
            tripInfo.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }

        try {
            String update = "UPDATE Trips SET end_time = \'%s\', fee = %d WHERE id = %d";
            update = String.format(update, formatter.format(timestamp),diffMin, tripId);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(update);
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }

        try {
            String tripInfo = "SELECT T.id, P.name, T.start_time, T.end_time, T.fee FROM Trips T, Passengers P WHERE T.id = %d AND T.passenger_id = P.id";
            tripInfo = String.format(tripInfo, tripId);
            Statement stmt = conn.createStatement();
            ResultSet displayTrip = stmt.executeQuery(tripInfo);
            System.out.println("Trip ID, Passenger name, Start, End, Fee");
            while (displayTrip.next()) {
                int Id = displayTrip.getInt(1);
                String passengerName = displayTrip.getString(2);
                Timestamp tempStartTime = displayTrip.getTimestamp(3);
                String startTime = formatter.format(tempStartTime);
                Timestamp tempEndTime = displayTrip.getTimestamp(4);
                String endTime = formatter.format(tempEndTime);
                int fee = displayTrip.getInt(5);
                System.out.println(Id + ", " + passengerName + ", " + startTime + ", " + endTime + ", " + fee);
            }
            displayTrip.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validId(String table, String field, int id){
        ArrayList<Integer> validList = new ArrayList<>();
        try{
            String validSQL = "SELECT %s FROM %s WHERE %s = %d";
            validSQL = String.format(validSQL,field, table, field, id);
            Statement stmt = conn.createStatement();
            ResultSet validInfo = stmt.executeQuery(validSQL);
            while(validInfo.next()){
                int info = validInfo.getInt(1);
                validList.add(info);
            }
            validInfo.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
        if (validList.contains(id))
            return true;
        else
            return false;
    }
}
