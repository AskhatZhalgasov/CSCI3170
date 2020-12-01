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
        System.out.println("Passenger, what would you like to do?");
        System.out.println("1. Reques a ride");
        System.out.println("2. Check trip records");
        System.out.println("3. Go back");

        while(true) {
            System.out.println("Please enter[1-3]");
            input = in.nextInt();
            if (1 <= input && input <= 3) {
                break;
            } else {
                System.out.println("[ERROR] Invalid input.");
            }
        }
        if (input == 1)
        {
            make_request();
        }
        else if (input == 2)
        {
            check_trip();
        }

    }
    public void make_request()
    {
        int p_id, pnum, d_years;
        String start_loc, end_loc, model;
        String dd_years;
        System.out.println("Please enter your ID.");
        p_id = in.nextInt();
        System.out.println("Please enter the number of passengers.");
        pnum = in.nextInt();
        in.nextLine();
        System.out.println("Please enter the start location.");
        start_loc = in.nextLine();
        System.out.println("Please enter the destination.");
        while (true)
        {
            end_loc = in.nextLine();
            if (end_loc == start_loc)
            System.out.println("[ERROR] destination and start location should be different.");
            else
            break;
        }
        System.out.println("Please enter the model. (Press enter to skip)");
        model = in.nextLine();
        System.out.println("Please enter the minimum driving years of the drivers. (Press enter to skip)");
        dd_years = in.nextLine();
        if (dd_years.length()==0)
            d_years = 0;
        else
            d_years = Integer.parseInt(dd_years);
        PreparedStatement stmt,stmt2;
        try
        {
            if (model.length()!=0) {
                stmt2 = conn.prepareStatement(
                        "insert into requests (id,taken,model,passengers,start_location,finish_location) values (" + Main.req_id + ",0,"+ model + "," + pnum + "," + start_loc + "," + end_loc + ",)"); 
            } else {
                stmt2 = conn.prepareStatement(
                    "insert into requests (id,taken,model,passengers,start_location,finish_location) values (" + Main.req_id + ",0," + ",null," + pnum + "," + start_loc + "," + end_loc + ")");
            }
            Main.req_id++;
        }
        catch (SQLException e) 
        {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }
        try 
        {
            if (model.length()!=0) {
                stmt = conn.prepareStatement(
                        "select count(case when vehicles.seats >="
                                + pnum + " and driving_years>=" + d_years + " and vehicles.model like \'" + model + "\' then 1 else null end) as num from drivers full join vehicles on vehicle_id=vehicles.id");
            } else {
                stmt = conn.prepareStatement(
                        "select count(case when vehicles.seats >="
                                + pnum + " and driving_years>=" + d_years + " then 1 else null end) as num from drivers full join vehicles on vehicle_id=vehicles.id");
            }
            ResultSet res = stmt.executeQuery();
            res.next();
            int count = res.getInt(1);
            System.out.println("Your request is placed. " + count + " are able to take request.");
        } 
        catch (SQLException e) 
        {
            System.out.println("[ERROR] " + e);
            System.exit(1);
        }
    }
    public void check_trip()
    {
        int id;
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date x=new Date();
        Timestamp start_time, finish_time;
        String date, destination;
        System.out.println("Please enter your ID.");
        id = in.nextInt();
        in.nextLine();
        System.out.println("Please enter the start date.");
        date = in.nextLine();
        date += " 00:00:00";
        
        try
        {
            x=dateFormat.parse(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        System.out.println(x);
        start_time = new Timestamp(x.getTime());
        System.out.println("Please enter the end date.");
        date = in.nextLine();
        date += " 23:59:59";
        try
        {
           x=dateFormat.parse(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        System.out.println("-------------"+date);
        System.out.println(x);
        finish_time=new Timestamp(x.getTime());
        System.out.println("Please enter the destination.");
        destination = in.nextLine();
        PreparedStatement stmt;
        try 
        {
            //надо протестировать
                stmt = conn.prepareStatement(
                        "select * from trips where passenger_id=" + id + " and timestampdiff(DAY,\'"+start_time+"\',start_time)>0 and timestampdiff(DAY,\'"+finish_time+"\',finish_time)>0");
                stmt.execute();
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
    }
}