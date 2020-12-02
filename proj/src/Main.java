import java.util.*;
//import java.sql.*;
//java -cp .:C:Users:User:Desktop:Study:Y3 SEM1:CSCI 3170:mysql-connector-java-5.1.49.jar Main

public class Main {

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
	    Scanner scanner = new Scanner(System.in);
	    int identity = scanner.nextInt();
            switch (identity) {
                case 1:
                    Admin admin = new Admin();
                    admin.start();
                    break;

                case 2:
                    //Passenger passenger = new Passenger();
                    //passenger.initMessage();
                    break;

                case 3:
                    //Driver driver = new Driver();
                    //driver.initMessage();
                    break;

                case 4:
                    //Manager manager = new Manager();
                    break;

                case 5:
                    end = true;
                    break;
            }
        }
    }

}