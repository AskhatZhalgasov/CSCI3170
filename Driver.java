import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Driver {

    final Connection conn;
    final Scanner in;
    int input;

    public Driver(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }

    public void start() {
        System.out.println("Driver, what would you like to do?");
        System.out.println("1. Search requests");
        System.out.println("2. Take a request");
        System.out.println("3. Finish a trip");
        System.out.println("4. Go back");

        while(true) {
            System.out.println("Please enter[1-4]");
            input = in.nextInt();
            if (1 <= input && input <= 3) {
                break;
            } else {
                System.out.println("[ERROR] Invalid input.");
            }
        }
        if (input == 1)
        {
            search_requests();
        }
        else if (input == 2)
        {
            take_request();
        }
        else if (input == 3)
        {
            //finish_trip();
        }

    }

     public static boolean isNullOrEmpty(String str) {
        if (str == null && str.length() == 0) 
            return true;
        return false;
    }

    public void search_requests() {
        int d_id, start_loc_x, start_loc_y, max_distance;
        System.out.println("Please enter your ID.");
        d_id = in.nextInt();
        System.out.println("Please enter the coordinates of your location.");
        start_loc_x = in.nextInt();
        start_loc_y = in.nextInt();
        System.out.println("Please enter the maximum distance from you to the passenger.");
        max_distance = in.nextInt();
        try
        {
            PreparedStatement stmt = conn.prepareStatement("select R.id, P.name, R.passengers, R.start_location, R.finish_location from requests as R, taxi_stops as TS, taxi_stops as TT, passengers as P where ");
        } 
        catch (SQLException e) 
        {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }
    }
    public void take_request() {
        int d_id, r_id;
        System.out.println("Please enter your ID.");
        d_id = in.nextInt();
        System.out.println("Please enter the request id.");
        r_id = in.nextInt();
        try
        {
            PreparedStatement stmt_dr, stmt_rq;
            stmt_dr = conn.prepareStatement("select * from drivers as D, vehicles as V where D.id = " + d_id + " and D.vehicle_id = V.id");
            stmt_rq = conn.prepareStatement("select * from requests where id = " + r_id);
            ResultSet dr_set = stmt_dr.executeQuery();
            ResultSet rq_set = stmt_rq.executeQuery();
            int dr_years = 0, vh_capacity = 0, dr_years_required = 0, passengers_num = 0, dr_id = 0, passenger_id = 0;
            StringBuffer model_ = new StringBuffer("");
            StringBuffer model_required_ = new StringBuffer("");
            StringBuffer start_location = new StringBuffer("");
            StringBuffer finish_location = new StringBuffer("");
            while(dr_set.next()) {         
             dr_years = dr_set.getInt("driving_years");
             dr_id = dr_set.getInt("id");
            model_.append(dr_set.getString("model"));
             vh_capacity = dr_set.getInt("seats");
            }
            while(rq_set.next()) {    
            model_required_.append(rq_set.getString("model"));
             passenger_id = rq_set.getInt("passenger_id");
             dr_years_required = rq_set.getInt("driving_years");
             start_location.append(rq_set.getString("start_location"));
             finish_location.append(rq_set.getString("finish_location"));
             passengers_num = rq_set.getInt("passengers");
            }
            String model = model_.toString();
            String model_required = model_required_.toString();
            //System.out.println(model_required);
            //System.out.println(model);
            //System.out.println("" + vh_capacity + ", " + passengers_num + ", " + dr_years + ", " + dr_years_required + ", " +  isNullOrEmpty(model_required) + ", " + model_required.length() + ", " + model_required.equals("null") );
            if(vh_capacity >= passengers_num && dr_years >= dr_years_required && (model_required.equals("null") || model_required.equals(model))) {
                System.out.println("Yes baby");
                //                                                                                                                                                                                          2018-07-04 14:33:20
                PreparedStatement stmt_tr = conn.prepareStatement("insert into trips (driver_id,passenger_id,start_time,start_location, finish_location, fee) values ("+ dr_id + "," + passenger_id + ",\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\",\"" + start_location + "\",\"" + finish_location + "\", 0)");   
                stmt_tr.execute();
            }
        }
        catch (SQLException e) 
        {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }

    }
    
}