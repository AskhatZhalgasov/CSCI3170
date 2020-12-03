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
        while(true) {

            System.out.println("Driver, what would you like to do?");
            System.out.println("1. Search requests");
            System.out.println("2. Take a request");
            System.out.println("3. Finish a trip");
            System.out.println("4. Go back");    
            while(true) {
                System.out.println("Please enter[1-4]");
                input = Main.getInt(this.in);
                if (1 <= input && input <= 4) {
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
                finish_trip();
            }
            else {
                break;
            }
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
            //PreparedStatement stmt = conn.prepareStatement("select R.id, P.name, R.passengers, R.start_location, R.finish_location from requests as R, taxi_stops as TS, taxi_stops as TT, passengers as P, drivers as D, vehicles as V where D.id = " + d_id + " and D.vehicle_id = V.id and TS.name = R.start_location and (ABS(TS.x - " + start_loc_x + ") + ABS(TS.y - " + start_loc_y + ")) between 0 and " + max_distance + " and D.driving_years > R.driving_years and (R.model = \"null\" )");
            PreparedStatement stmt = conn.prepareStatement("select R.id, P.name, R.passengers, R.start_location, R.finish_location from requests as R, taxi_stops as TS, passengers as P, drivers as D, vehicles as V where R.taken = 0 and D.id = " + d_id + " and P.id = R.passenger_id and D.vehicle_id = V.id and TS.name = R.start_location and (ABS(TS.x - " + start_loc_x + ") + ABS(TS.y - " + start_loc_y + ")) between 0 and " + max_distance + " and D.driving_years > R.driving_years and (R.model is NULL or V.model like concat('%', R.model, '%'))");          
            //System.out.println("select R.id, P.name, R.passengers, R.start_location, R.finish_location from requests as R, taxi_stops as TS, taxi_stops as TT, passengers as P, drivers as D, vehicles as V where D.id = " + d_id + " and P.id = R.passenger_id and D.vehicle_id = V.id and TS.name = R.start_location and (ABS(TS.x - " + start_loc_x + ") + ABS(TS.y - " + start_loc_y + ")) between 0 and " + max_distance + " and D.driving_years > R.driving_years and (R.model = \"null\" or R.model = V.model )");
            //PreparedStatement stmt = conn.prepareStatement("select * from requests");
            ResultSet res = stmt.executeQuery();
            ResultSetMetaData rsmd = res.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            //int rowCount = rsmd.getRowCount();
            //System.out.println(columnsNumber);
            //System.out.println("trip id, driver name, passenger name, start location, destination, duration");
            System.out.println("request ID, passenger name, num of passengers, start location, destination");
            while (res.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue;              
                    columnValue = res.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println("");
            }
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
                //System.out.println("Yes baby");
                //                                                                                                                                                                                          2018-07-04 14:33:20
                PreparedStatement stmt_tr = conn.prepareStatement("insert into trips (driver_id,passenger_id,start_time, start_location, finish_location, fee) values ("+ dr_id + "," + passenger_id + ",\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\",\"" + start_location + "\",\"" + finish_location + "\", 0)");   
                stmt_tr.execute();
                PreparedStatement stmt_update_rq = conn.prepareStatement("update requests set taken = 1 where id = " + r_id);
                stmt_update_rq.execute();
                PreparedStatement stmt_search_trip = conn.prepareStatement("select T.id, P.name, T.start_time from trips as T, passengers as P where T.driver_id = " + d_id + " and T.passenger_id = " + passenger_id + " and T.passenger_id = P.id and TIMESTAMPDIFF(SECOND, T.start_time, \"" +new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\") between 0 and 180");
                ResultSet search_trip_res = stmt_search_trip.executeQuery();
                ResultSetMetaData rsmd_st = search_trip_res.getMetaData();
                int st_columnsNumber = rsmd_st.getColumnCount();
                System.out.println("Trip ID, Passenger name, Start");
                while(search_trip_res.next()) {
                    for (int i = 1; i <= st_columnsNumber; i++) {
                        if (i > 1) System.out.print(",  ");
                        String columnValue_;              
                        columnValue_ = search_trip_res.getString(i);
                        System.out.print(columnValue_);
                    }
                    System.out.println("");
                } 
                //System.out.println("The request was taken! Enjoy your trip");
            }
        }
        catch (SQLException e) 
        {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }

    }
    public void finish_trip() {
        int d_id;
        System.out.println("Please enter your ID.");
        d_id = in.nextInt();
        try {
            PreparedStatement stmt = conn.prepareStatement("select id, passenger_id, start_time from trips where driver_id = " + d_id + " and finish_time is NULL");
            //PreparedStatement stmt = conn.prepareStatement("select * from trips where id = 501");
            ResultSet res = stmt.executeQuery();
            ResultSetMetaData rsmd = res.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            int r_id = 0;
            StringBuffer start_time = new StringBuffer("");
            System.out.println("Trip ID, Passenger ID, Start");
            while (res.next()) {
                r_id = res.getInt("id");
                start_time.append(res.getString("start_time"));
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");                    
                    String columnValue;              
                    columnValue = res.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println("");
            }
            System.out.println("Do you wish to finish the trip");
            char answer = in.next().charAt(0);
            if(answer == 'y') {
                String finish_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                PreparedStatement stmt_finish_ = conn.prepareStatement("update trips set start_time = \"" + start_time + "\", finish_time = \"" + finish_time + "\", fee = TIMESTAMPDIFF(MINUTE, \"" + start_time + "\",\"" + finish_time + "\") where id = " + r_id + "");
                stmt_finish_.execute();
                PreparedStatement stmt_finish = conn.prepareStatement("select T.id, P.name, T.start_time, T.finish_time, T.fee from trips as T, passengers as P where T.id = " + r_id + " and T.passenger_id = P.id ");
                //System.out.println("update trips set finish_time = \"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\", fee = TIMESTAMPDIFF(MINUTE, start_time, \"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\") where trips.id = " + r_id + "");
                ResultSet x = stmt_finish.executeQuery();
                ResultSetMetaData _rsmd = x.getMetaData();
                int _columnsNumber = _rsmd.getColumnCount();
                System.out.println("Trip ID, Passenger name, Start, End, Fee");
                while(x.next()) {
                    for (int i = 1; i <= _columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String _columnValue;              
                    _columnValue = x.getString(i);
                    System.out.print(_columnValue);
                    }
                    System.out.println("");
                }
            }
            //UPDATE `zz` SET `sa` = '2' WHERE `zz`.`sa` = 1
        } catch (SQLException e) 
        {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }
    }
    
}