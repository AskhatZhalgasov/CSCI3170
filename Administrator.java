import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Administrator {

    final Connection conn;
    final Scanner in;

    public Administrator(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }
    
}