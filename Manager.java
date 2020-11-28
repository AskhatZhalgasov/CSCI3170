import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Manager {

    final Connection conn;
    final Scanner in;

    int input;
    int minDistance;
    int maxDistance;

    public Manager(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }

    public start() {
        System.out.println("Manager, what would you like to do?");
        System.out.println("1. Find trips");
        System.out.println("2. Go back");

        whilte(true) {
            System.out.println("Please enter[1-2]");
            input = in.nextInt();
            if (1 <= input && input <= 2) {
                break;
            } else {
                System.out.println("[ERROR] Invalid input.");
            }
        }

        if (input != 2) {
            System.out.println("Please enter the minimum traveling distance.");
            minDistance = in.nextInt();
            System.out.println("Please enter the maximum traveling distance.");
            maxDistance = in.nextInt();
            
        }

    }
    
}