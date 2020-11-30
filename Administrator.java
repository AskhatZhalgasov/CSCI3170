import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Administrator {

    final Connection conn;
    final Scanner in;
    int input;

    final String[] names = {
            "vehicles",
            "passengers",
            "drivers",
            "trips",
            "requests",
            "taxi_stops", 
    };
 

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
                conn.prepareStatement("create table trips ( id integer not null primary key, driver_id integer not null, passenger_id integer not null, start_time date not null, finish_time date, start_location varchar(20) not null, finish_location varchar(20) not null, fee integer not null );"),
                conn.prepareStatement("create table requests ( id integer not null primary key, taken integer not null, model varchar(30), passengers integer not null, start_location varchar(20) not null, finish_location varchar(20) not null);")
            };

            for (int i = 0; i < stmts.length; i++) {
                stmts[i].execute();
            }
        } catch(SQLException e) {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }
        System.out.println("Processing... Done! Tables are created!\n");
    }

    private void deleteTables() {
        try {
            for (String name : this.names) {
                PreparedStatement stmt = conn.prepareStatement("drop table if exists " + name);
                stmt.execute();
            }
        } catch(SQLException e) {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }
        System.out.println("Processing... Done! Tables are deleted\n");
    }

    private void loadData() {
        System.out.println("Please enter the folder path");
        String folderPath = in.next();

        try {
            loadDriversData(folderPath + "/drivers.csv");
            loadPassengersData(folderPath + "/passengers.csv");
            loadTaxiStopsData(folderPath + "/taxi_stops.csv");
            loadTripsData(folderPath + "/trips.csv");
            loadVehiclesData(folderPath + "/vehicles.csv");
        } catch(Exception e) {
            System.out.println("[ERROR] " + e);
        }

        System.out.println("Processing...Data is loaded!\n");
    }



    private void loadDriversData(String filePath) throws Exception {
        PreparedStatement stmt = conn.prepareStatement("insert into drivers (id, name, vehicle_id, driving_years) values (?, ?, ?, ?)");

        Scanner scanner = new Scanner(new File(filePath));
        while(scanner.hasNext()) {
            String[] values = (scanner.nextLine()).split(",");

            for (int i = 0; i < values.length; i++) {
                stmt.setString(i + 1, values[i]);
            }

            stmt.execute();
        }

        System.out.println("Drivers data is loaded...");
    }

    private void loadVehiclesData(String filePath) throws Exception {
        PreparedStatement stmt = conn.prepareStatement("insert into vehicles (id, model, seats) values (?, ?, ?)");

        Scanner scanner = new Scanner(new File(filePath));
        while(scanner.hasNext()) {
            String[] values = (scanner.nextLine()).split(",");

            for (int i = 0; i < values.length; i++) {
                stmt.setString(i + 1, values[i]);
            }

            stmt.execute();
        }

        System.out.println("Vehicles data is loaded...");
    }

    private void loadPassengersData(String filePath) throws Exception {
        PreparedStatement stmt = conn.prepareStatement("insert into passengers (id, name) values (?, ?)");

        Scanner scanner = new Scanner(new File(filePath));
        while(scanner.hasNext()) {
            String[] values = (scanner.nextLine()).split(",");

            for (int i = 0; i < values.length; i++) {
                stmt.setString(i + 1, values[i]);
            }

            stmt.execute();
        }

        System.out.println("Passengers data is loaded...");
    }

    private void loadTripsData(String filePath) throws Exception {
        PreparedStatement stmt = conn.prepareStatement("insert into trips (id, driver_id, passenger_id, start_time, finish_time, start_location, finish_location, fee) values (?, ?, ?, ?, ?, ?, ?, ?)");

        Scanner scanner = new Scanner(new File(filePath));
        while(scanner.hasNext()) {
            String[] values = (scanner.nextLine()).split(",");

            for (int i = 0; i < values.length; i++) {
                stmt.setString(i + 1, values[i]);
            }

            stmt.execute();
        }

        System.out.println("Trips data is loaded...");
    }

    private void loadTaxiStopsData(String filePath) throws Exception {
        PreparedStatement stmt = conn.prepareStatement("insert into taxi_stops (name, x, y) values (?, ?, ?)");

        Scanner scanner = new Scanner(new File(filePath));
        while(scanner.hasNext()) {
            String[] values = (scanner.nextLine()).split(",");

            for (int i = 0; i < values.length; i++) {
                stmt.setString(i + 1, values[i]);
            }

            stmt.execute();
        }

        System.out.println("Taxi stops data is loaded...");
    }

    private void checkData() {
        String[] output = { "Vehicles:", "Passenger:", "Driver:", "Trip:", "Request:", "Taxi_Stop:" };
        try {
            PreparedStatement stmt;
            for (int i = 0; i < output.length; i++) {
                stmt = conn.prepareStatement("select count(*) from " + this.names[i]);
                ResultSet query = stmt.executeQuery();
                query.next();
                int result = query.getInt(1);
                System.out.println(output[i] + result);
            }
            System.out.println();
        } catch(SQLException e) {
            System.out.println("[ERROR] Cannot query the size of tables, probably the tables do not exist.");
            System.exit(1);
        }
    }
}