import java.util.Scanner;
import java.sql.*;

public class Main {

    // sometimes we finish the programm due to timeout
    public static int getInt(Scanner in) {
        int result = 0;
        try {
            result = in.nextInt();
            return result;
        } catch(Exception e) {
            System.out.println("[ERROR] input is not integer.");
            System.exit(-1);
        }
        return -1;
    }

    public static String getLine(Scanner in) {
        try {
            String result = in.nextLine();
            return result;
        } catch(Exception e) {
            System.out.println("[ERROR] input is not string.");
            System.exit(-1);
        }
        return null;
    }

    public static void main(String[] args) {
        // Firstly, need to connect to the database 
        String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group14";
        String dbUsername = "Group14";
        String dbPassword = "3170group14";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
        } catch(ClassNotFoundException e) {
            System.out.println("[ERROR]: Java MySql DB Driver not found!!");
            System.exit(0);
        } catch(SQLException e) {
            System.out.println("[ERROR]: " + e);
        }

        System.out.println("Connected!");

        System.out.println("Starting of the program");
        TaxiSystem system = new TaxiSystem(con);
        system.start();
    }
}