import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;

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

    public void start() {
        System.out.println("Manager, what would you like to do?");
        System.out.println("1. Find trips");
        System.out.println("2. Go back");

        while(true) {
            while(true) {
                System.out.println("Please enter[1-2]");
                input = Main.getInput(this.in);
                if (1 <= input && input <= 2) {
                    break;
                } else {
                    System.out.println("[ERROR] Invalid input.");
                }
            }

            if (input == 1) {
                findTrips();
            } else if (input == 2) {
                break;
            } 
        }

    }
    private void findTrips() {
        int min, max;
        try{
            System.out.println("Please enter the minimum travelling distance");
            min = in.nextInt();
            System.out.println("Please enter the maximum travelling distance");
            max = in.nextInt();
        } catch(InputMismatchException e) {
            System.out.println("Input is not an int value"); 
            return;
        }
        if(min > max || max < min || min < 0 || max < 0) {
            System.out.println("Please input the distances correctly");
            return;
        }
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT T.id, P.name, D.name, T.start_location, T.finish_location, TIMESTAMPDIFF(MINUTE, T.start_time, T.finish_time) as duration FROM trips T, taxi_stops TS, taxi_stops TT, drivers D, passengers P WHERE T.start_location = TS.name and P.id = T.passenger_id and T.finish_location = TT.name and T.driver_id = D.id and (ABS(TT.x - TS.x) + ABS(TT.y - TS.y)) between ? and ?");
            //, DATEDIFF(minute, T.finish_time, T.start_time) as duration
            stmt.setInt(1, min);
            stmt.setInt(2, max);
            ResultSet res = stmt.executeQuery();
            ResultSetMetaData rsmd = res.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            System.out.println("trip id, driver name, passenger name, start location, destination, duration");
            while (res.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue;              
                    columnValue = res.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println("");
            }
        } catch(SQLException e) {
            System.out.println("[ERROR] Cannot query the size of tables, probably the tables do not exist.");
            System.out.println(e);
            System.exit(1);
        }
    }
    
}