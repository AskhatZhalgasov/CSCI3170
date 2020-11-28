import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Passenger {

    final Connection conn;
    final Scanner in;

    public Passenger(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }
    
}