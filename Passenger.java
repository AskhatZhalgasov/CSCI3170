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
            /* не уверен насчет пропуска 2 последних вводов */
        int p_id, pnum, d_years;
        String start_loc, end_loc, model;
        String dd_years;
        System.out.println("Please enter your ID.");
        p_id = in.nextInt();
        System.out.println("Please enter the number of passengers.");
        pnum = in.nextInt();
        System.out.println("Please enter the start location.");
        start_loc = in.nextLine();
        System.out.println("Please enter the destination.");
        end_loc = in.nextLine();
        System.out.println("Please enter the model. (Press enter to skip)");
        model = in.nextLine();
        System.out.println("Please enter the minimum driving years of the driver. (Press enter to skip)");
        dd_years = in.nextLine();
        if (dd_years.charAt(0) == '\n')
            d_years = 0;
        else
            d_years = Integer.parseInt(dd_years);
        PreparedStatement stmt;
        try 
        {
            /* короче тут надо немного над querry поработать, не тестировал */
            if (model != "\n") {
                stmt = conn.prepareStatement(
                        "select (*) from driver full join vehicle on driver.vehicle_id = vehicle.id count(*) where seats >="
                                + pnum + "and driving_years >= " + d_years + " and model like " + model);
            } else {
                stmt = conn.prepareStatement(
                        "select (*) from driver full join vehicle on driver.vehicle_id = vehicle.id count(*) where seats >="
                                + pnum + "and driving_years >= " + d_years);
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
        SimpleDateFormat dateFormat=new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
        Date start_time=null,end_time=null;
        String date, destination;
        System.out.println("Please enter your ID.");
        id = in.nextInt();
        System.out.println("Please enter the start date.");
        date = in.nextLine();
        date += " 00:00:00";
        try
        {
            start_time=dateFormat.parse(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        System.out.println("Please enter the end date.");
        date = in.nextLine();
        date += " 23:59:59";
        try
        {
            end_time=dateFormat.parse(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        System.out.println("Please enter the destination.");
        destination = in.nextLine();
        PreparedStatement stmt;
        try 
        {
            /* не тестировал */
                stmt = conn.prepareStatement(
                        "select * from trip where passenger_id = " + id + " and start_time >= " + start_time + " and end_time <= " + end_time);
                stmt.execute();
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
    }
}