import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Passenger {

    final Connection conn;
    final Scanner in;
    int input;

    public Passenger(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }

    public void start() {
        while(true) {
            System.out.println("Passenger, what would you like to do?");
            System.out.println("1. Request a ride");
            System.out.println("2. Check trip records");
            System.out.println("3. Go back");
            while (true) {
                System.out.println("Please enter[1-3]");
                input = Main.getInt(this.in);
                if (1 <= input && input <= 3) {
                    break;
                } else {
                    System.out.println("[ERROR] Invalid input.");
                }
            }
            if (input == 1) {
                make_request();
            } else if (input == 2) {
                check_trip();
            } else {
                break;
            }
        }
    }

    public void make_request() {
        int p_id, pnum, d_years;
        String start_loc, end_loc, model;
        String dd_years;
        while (true){
            System.out.println("Please enter your ID.");
            p_id = Main.getInt(this.in);
            PreparedStatement stmtc;
            try {
                stmtc = conn.prepareStatement("select count(case when passenger_id =" + p_id
                        + " and taken = 0 then 1 else null end) as num from requests");
                ResultSet res = stmtc.executeQuery();
                res.next();
                int count = res.getInt(1);
                if (count != 0) {
                    System.out.println("[ERROR] the passenger with this Id has open request");
                } else
                    break;
            }
            catch (SQLException e) {
                System.out.println("[ERROR] " + e);
                System.exit(1);
            }
        }
        System.out.println("Please enter the number of passengers.");
        while (true)
        {
            pnum = Main.getInt(this.in);
            if (pnum>8 || pnum < 1)
                System.out.println("[Error] Invalid number of passengers");
            else 
                break;
        }
        in.nextLine();
        while (true) {
            System.out.println("Please enter the start location.");
            start_loc = Main.getLine(this.in);
            PreparedStatement stmtc;
            try {
                stmtc = conn.prepareStatement("select count(case when name like \'" + start_loc
                        + "\' then 1 else null end) as num from taxi_stops");
                ResultSet res = stmtc.executeQuery();
                res.next();
                int count = res.getInt(1);
                if (count == 0) {
                    System.out.println("[ERROR] Start Location not found");
                } else
                    break;
            } catch (SQLException e) {
                System.out.println("[ERROR] " + e);
                System.exit(1);
            }
        }
        while (true) {
            System.out.println("Please enter the destination.");
            end_loc = Main.getLine(this.in);
            PreparedStatement stmtc;
            try {
                stmtc = conn.prepareStatement("select count(case when name like \'" + end_loc
                        + "\' then 1 else null end) as num from taxi_stops");
                ResultSet res = stmtc.executeQuery();
                res.next();
                int count = res.getInt(1);
                if (count == 0) {
                    System.out.println("[ERROR] Destination not found");
                } else if (end_loc.equals(start_loc))
                    System.out.println("[ERROR] Destination and start location should be different.");
                else
                    break;
            } catch (SQLException e) {
                System.out.println("[ERROR] " + e);
                System.exit(1);
            }
        }
        System.out.println("Please enter the model. (Press enter to skip)");
        model = Main.getLine(this.in);
        System.out.println("Please enter the minimum driving years of the drivers. (Press enter to skip)");
        dd_years = Main.getLine(this.in);
        if (dd_years.length() == 0)
            d_years = 0;
        else
            d_years = Integer.parseInt(dd_years);
        PreparedStatement stmt, stmt2;
        try {
            if (model.length() != 0) {
                stmt2 = conn.prepareStatement(
                        "insert into requests (taken,model,driving_years,passenger_id,passengers,start_location,finish_location) values (0,\'"
                                + model + "\'," + d_years + "," + p_id + "," + pnum + ",\'" + start_loc + "\',\'"
                                + end_loc + "\')");
            } else {
                stmt2 = conn.prepareStatement(
                        "insert into requests (taken,model,driving_years,passenger_id,passengers,start_location,finish_location) values (0,null,"
                                + d_years + "," + p_id + "," + pnum + ",\'" + start_loc + "\',\'" + end_loc + "\')");
            }
            stmt2.execute();
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }
        try {
            if (model.length() != 0) {
                stmt = conn.prepareStatement("select count(case when vehicles.seats >=" + pnum + " and driving_years>="
                        + d_years + " and vehicles.model like \'%" + model
                        + "%\' then 1 else null end) as num from drivers cross join vehicles on vehicle_id=vehicles.id");
            } else {
                stmt = conn.prepareStatement("select count(case when vehicles.seats >=" + pnum + " and driving_years>="
                        + d_years
                        + " then 1 else null end) as num from drivers cross join vehicles on vehicle_id=vehicles.id");
            }
            ResultSet res = stmt.executeQuery();
            res.next();
            int count = res.getInt(1);
            System.out.println("Your request is placed. " + count + " drivers are able to take request.");
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }
    }

    public void check_trip()
    {
        int id;
        String start_time, finish_time;
        String date, destination;
        System.out.println("Please enter your ID.");
        id = Main.getInt(this.in);
        Main.getLine(this.in);
        System.out.println("Please enter the start date.");
        date = Main.getLine(this.in);
        date += " 00:00:00";
        start_time=date;
        System.out.println("Please enter the end date.");
        date = Main.getLine(this.in);
        date += " 23:59:59";
        finish_time=date;
        System.out.println("Please enter the destination.");
        destination = Main.getLine(this.in);
        PreparedStatement stmt;
        try 
        {
                String st1,st2;
                st1="select T.id,D.name,V.id,V.model,start_time,finish_time,fee,start_location,finish_location ";
                st2="from trips as T cross join drivers as D on driver_id = D.id cross join vehicles as V on D.vehicle_id like V.id ";
                stmt = conn.prepareStatement(
                        st1+st2+"where T.passenger_id=" + id + " and timestampdiff(DAY,\'"+start_time+"\',T.start_time)>0 and timestampdiff(DAY,T.finish_time,\'"+finish_time
                        + "\')>0 and T.finish_location like \'" + destination + "\' order by start_time");                
                ResultSet res = stmt.executeQuery();
                ResultSetMetaData rsmd = res.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                System.out.println("Trip id, Driver Name , Vehicle ID, Vehicle Model, Start, End, Fee, Start Location, Destination");
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
        catch(SQLException e)
        {
            System.out.println("[ERROR1] " + e);
            System.exit(1);
        }
    }
}
/*
 * 2 2 1 2018-01-01 2018-12-31 Sham Shui Po
 */