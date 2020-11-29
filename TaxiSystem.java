import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class TaxiSystem {

    final Connection conn;
    final Scanner in;

    public TaxiSystem(Connection conn) {
        this.in = new Scanner(System.in);
        this.conn = conn;
    }
    
    int getInput() {
        System.out.println("Welcome! Who are you?");
        System.out.println("1. An administrator");
        System.out.println("2. A passenger");
        System.out.println("3. A driver");
        System.out.println("4. A manager");
        System.out.println("5. None of the above");
        System.out.println("Please enter [1-4]");

        return in.nextInt();
    }

    public void start() {
        int input;
        while((input = getInput()) != 5) {
            //System.out.println("input : " + input);
            switch(input) {
                case 1:
                    (new Administrator(conn, in)).start();
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    (new Manager(conn, in)).start();
                    break;
                default: 
                    System.out.println("[ERROR] Invalid input! Please retry.");
            }
        }
    }
}