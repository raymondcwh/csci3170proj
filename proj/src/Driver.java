package com.testing;

import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class Driver {
    boolean validInput = true;
    Connection conn = Main.connect();

    public void initMessage(){
        System.out.println("Driver, what would you like to do?");
        System.out.println("1. Search requests");
        System.out.println("2. Take a request");
        System.out.println("3. Finish a trip");
        System.out.println("4. Go back");
        do {
            System.out.println("Please enter [1-4]");
            Scanner scanner = new Scanner(System.in);
            int action = scanner.nextInt();
            switch (action) {
                case 1:
                    this.validInput = true;
                    this.searchRequests();
                    break;

                case 2:
                    this.validInput = true;
                    this.takeRequest();
                    break;

                case 3:
                    this.validInput = true;
                    this.finishTrip();
                    break;

                case 4:
                    break;

                default:
                    System.out.println("[ERROR] Invalid input.");
                    this.validInput = false;
            }
        }while(!validInput);
    }

    public void searchRequests(){
        System.out.println("Please enter your ID.");
        Scanner scanner = new Scanner(System.in);
        int driverId = scanner.nextInt();
        System.out.println("Please enter the coordinates of your location.");
        int locX = scanner.nextInt();
        int locY = scanner.nextInt();
        System.out.println("Please enter the maximum distance from you to the passenger.");
        int maxDistance = scanner.nextInt();
        System.out.println(driverId+","+locX+","+locY+","+maxDistance);
        this.searchRequestsSql(driverId,locX,locY,maxDistance);
    }

    public void takeRequest(){
        System.out.println("Please enter your ID.");
        Scanner scanner = new Scanner(System.in);
        int driverId = scanner.nextInt();
        System.out.println("Please enter the request ID.");
        int requestId = scanner.nextInt();
        System.out.println(driverId+","+requestId);
        System.out.println("request ID, passenger name, num of passengers, start location, destination");
        this.takeRequestSQL(driverId,requestId);
    }

    public void finishTrip(){
        System.out.println("Please enter your ID.");
        Scanner scanner = new Scanner(System.in);
        int driverId = scanner.nextInt();

        this.unfinishedTripSql(driverId);

        scanner.nextLine();
        do {
            System.out.println("Do you wish to finish the trip? [y/n]");
            String yesNo = scanner.nextLine();
            switch (yesNo) {
                case "y":
                    this.validInput = true;
                    this.finishTripSql();
                    break;

                case "n":
                    this.validInput = true;
                    break;

                default:
                    System.out.println("[ERROR] Invalid input.");
                    this.validInput = false;
            }
        }while(!validInput);
    }

    public void searchRequestsSql (int driverId,int locX,int locY,int maxDistance){
        //get [driving_years, model, seats]
        int driving_years = -1;
        String model = "";
        int seats = -1;
        try{
            String sqlDriverInfo = "SELECT D.driving_years,V.model,V.seats FROM driver D, vehicle V WHERE D.id = %d AND V.id = D.vehicle_id ";
            sqlDriverInfo = String.format(sqlDriverInfo,driverId);
            Statement stmt = conn.createStatement();
            ResultSet driverInfo = stmt.executeQuery(sqlDriverInfo);
            while(driverInfo.next()){
                driving_years = driverInfo.getInt(1);
                model = driverInfo.getString(2);
                model = model.toLowerCase();
                seats = driverInfo.getInt(3);
            }
            driverInfo.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }

        String sqlRequestInfo = "SELECT R.id, P.name, R.passengers, R.start_location, R.destination FROM request R, passenger P, taxi_stop T" +
                " WHERE R.Driving_years <= %d AND  R.passengers <= %d AND CHARINDEX(R.model.toLowerCase(),%s)!=0 AND T.name = R.start_location AND R.passenger_id = P.id AND (abs(T.location_x-%d)+abs(T.location_y-%d)) <= %d" +
                " AND R.taken = false";
        try {
            sqlRequestInfo = String.format(sqlRequestInfo, driving_years, seats, model, locX, locY, maxDistance);
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

        //get [driving_years, model, seats]
        try{
        String sqlDriverInfo = "SELECT D.driving_years,V.model,V.seats FROM driver D, vehicle V WHERE D.id = %d AND V.id = D.vehicle_id ";
        sqlDriverInfo = String.format(sqlDriverInfo,driverId);
        Statement stmt = conn.createStatement();
        ResultSet driverInfo = stmt.executeQuery(sqlDriverInfo);
        while(driverInfo.next()){
            driving_years = driverInfo.getInt(1);
            model = driverInfo.getString(2);
            model = model.toLowerCase();
            seats = driverInfo.getInt(3);
        }
        driverInfo.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }

        try{
        String sqlRequestInfo = "SELECT R.id FROM request R, passenger P" +
                " WHERE R.Driving_years <= %d AND  R.passengers <= %d AND CHARINDEX(R.model.toLowerCase(),%s)!=0 AND R.passenger_id = P.id AND R.taken = false";
        sqlRequestInfo = String.format(sqlRequestInfo,driving_years,seats,model);
        Statement stmt = conn.createStatement();
        ResultSet requestInfo = stmt.executeQuery(sqlRequestInfo);
        System.out.println("request ID, passenger name, num of passengers, start location, destination");
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
        if(validList.contains(requestId)) {
            try {
                String update = "UPDATE request SET taken = true WHERE id = %d";
                update = String.format(update, requestId);
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(update);
            }catch(SQLException se){
                se.printStackTrace();
            }catch(Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("[ERROR] You do not fulfill request requirement.");
            return;
        }

        //Create trip
        try {
            String info = "SELECT R.passenger_id, R.start_location, R.destination FROM request R WHERE R.id = %d";
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
            String insert = "INSERT INTO trip (driver_id, passenger_id, start_location, destination, start_time) VALUES(%d, %d, %s, %s, %t)";
            insert = String.format(insert, requestId, passengerId, startLoc, destination, timestamp);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(insert);
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
            String tripInfo = "SELECT T.id, P.name, T.start_time FROM trip T, passenger P WHERE T.id = %d AND T.passenger_id = P.id";
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

    public void unfinishedTripSql (int driverId){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String unfinishedTrip = "SELECT T.id, T.passenger_id, T.start_time FROM trip T WHERE T.driver_id = %d AND T.end_time is NULL";
            unfinishedTrip = String.format(unfinishedTrip, driverId);
            Statement stmt = conn.createStatement();
            ResultSet displayUnfinishedTrip = stmt.executeQuery(unfinishedTrip);
            System.out.println("Trip ID, Passenger ID, Start");
            while (displayUnfinishedTrip.next()) {
                int Id = displayUnfinishedTrip.getInt(1);
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
    }

    public void finishTripSql (){
        System.out.println("Hi!");
    }
}

///ERROR handling !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!