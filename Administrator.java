import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Administrator {

    final Connection conn;
    final Scanner in;
    int input;

    public Administrator(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }

    public void start() {
        System.out.println("Administrator, what would you like to do?");
        System.out.println("1. Create tables");
        System.out.println("2. Delete tables");
        System.out.println("3. Load data");
        System.out.println("4. Check data");
        System.out.println("5. Go back");

        while(true) {
            System.out.println("Please enter[1-5]");
            input = in.nextInt();
            if (1 <= input && input <= 5) {
                break;
            } else {
                System.out.println("[ERROR] Invalid input.");
            }
        }

        if (input == 1) {
            createTables();
        } else if (input == 2) {
            deleteTables();
        } else if (input == 3) {
            loadData();
        } else if (input == 4) {
            checkData();
        }
    }

    private void createTables() {
        try {
            PreparedStatement[] stmts = {
                conn.prepareStatement("create table vehicles ( id char(6) not null primary key, model varchar(30) not null, seats integer not null );"),
                conn.prepareStatement("create table drivers ( id integer not null primary key, name varchar(30) not null, vehicle_id char(6) not null, driving_years integer not null );"),
                conn.prepareStatement("create table passengers ( id integer not null primary key, name varchar(30) not null );"),
                conn.prepareStatement("create table taxi_stops ( name varchar(20) not null primary key, x integer not null, y integer not null );"),
                conn.prepareStatement("create table trips ( id integer not null primary key, driver_id integer not null, passenger_id integer not null, start_time date not null, finish_time date, start_location varchar(20) not null, finish_location varchar(20) not null, fee integer not null );")
            };

            for (int i = 0; i < stmts.length; i++) {
                stmts[i].execute();
            }
        } catch(SQLException e) {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }
        System.out.println("Processing... Done! Tables are created!");
    }

    private void deleteTables() {
        String[] names = {
            "vehicles",
            "drivers",
            "trips",
            "taxi_stops", 
            "passengers"
        };
        try {
            for (int i = 0; i < names.length; i++) {
                PreparedStatement stmt = conn.prepareStatement("drop table if exists " + names[i]);
                stmt.execute();
            }
        } catch(SQLException e) {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }
        System.out.println("Processing... Done! Tables are deleted");
    }

    private void loadData() {
    }

    private void checkData() {

    }
}