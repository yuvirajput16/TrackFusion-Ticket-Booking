import java.sql.Connection;
import java.sql.DriverManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class admin {

    public static void main(String args[]) {
        Connection c = null;
        Statement stmt = null;

        try {

            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/train_reservation_system",
                            "postgres", "shyam2672002");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();

            String inputfile = "admin.txt";
            File queries = new File(inputfile);
            Scanner queryScanner = new Scanner(queries);
            String query = "";
            // --------------------------------------------------------------------

            // Read input queries and write to the output stream
            while (queryScanner.hasNextLine()) {
                query = queryScanner.nextLine();
                // query=" CALL ADDTRAIN(";
                String q = "CALL ADDTRAIN(";
                String s = "";
                int f = 0;
                StringTokenizer tokenizer = new StringTokenizer(query);
                while (tokenizer.hasMoreTokens()) {
                    s = tokenizer.nextToken();
                    if (s.charAt(0) == '#') {
                        String returnMsg = "ADMIN TERMINATED";

                        System.out.println(returnMsg);
                        stmt.close();
                        c.close();
                        
                        return;
                    }
                    if (f == 0) {
                        q = q + s + ",";
                    }
                    if (f == 1) {
                        q = q + "'" + s + "',";
                    }
                    if (f == 2) {
                        q = q + s + ",";
                    }
                    if (f == 3) {
                        q = q + s + ");";
                    }
                    f++;

                }
                System.out.println(q);
                try {
                    stmt.executeQuery(q);

                } catch (Exception e) {
                    // TODO: handle exception
                    System.out.println(e.getMessage());

                }

            }
            queryScanner.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }
}
