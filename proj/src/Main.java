import java.util.*;
import java.sql.*;
//import java.sql.*;
//java -cp .:C:Users:User:Desktop:Study:Y3 SEM1:CSCI 3170:mysql-connector-java-5.1.49.jar Main

// !!!!REMEMBER CHANGE BACK ALL RETURN ON HOLD STAGE AFTER PROCESSES

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    //private static Connection con = connect();
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

    public static void main(String[] args) {
        boolean end = false;
        while(!end) {
	        System.out.println("Welcome! Who are you?");
            System.out.println("1. An administrator");
            System.out.println("2. A passenger");
            System.out.println("3. A driver");
            System.out.println("4. A manager");
            System.out.println("5. None of the above");
            System.out.println("Please enter [1-4]");
	        // Scanner scanner = new Scanner(System.in);
	        int identity = scanner.nextInt();
            switch (identity) {
                case 1:
                    Admin.start();
                    break;

                case 2:
                    Passenger.start();
                    break;

                case 3:
                    Driver driver = new Driver();
                    driver.initMessage();
                    break;

                case 4:
                    Manager.initMessage();
                    break;
                case 5:
                    end = true;
                    break;
            }
            // scanner.close();
            System.out.println("");
        }
    }

}