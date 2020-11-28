import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Driver {

    final Connection conn;
    final Scanner in;

    public Driver(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }
    
}