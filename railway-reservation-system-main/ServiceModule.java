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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

class QueryRunner implements Runnable {
    // Declare socket for client access
    protected Socket socketConnection;

    public static int atoi(String str) {
        int res = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) <= '9' && str.charAt(i) >= '0')
                res = res * 10 + (str.charAt(i) - '0');
            else
                return res;
        }

        // return result.
        return res;
    }

    public static String func(String s) {
        if (s == "'sl'")
            return "'SL'";
        return "'AC'";
    }

    public static String generatepnr() {
        int min = 0;
        int max = 999999999;
        String res;

        // Generate random int value from 50 to 100
        // System.out.println("Random value in int from "+min+" to "+max+ ":");
        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
        // System.out.println(random_int);
        Integer f = random_int;
        res = f.toString();
        return res;
    }

    public QueryRunner(Socket clientSocket) {
        this.socketConnection = clientSocket;
    }

    public void run() {
        try {
            // Reading data from client
            InputStreamReader inputStream = new InputStreamReader(socketConnection
                    .getInputStream());
            BufferedReader bufferedInput = new BufferedReader(inputStream);
            OutputStreamWriter outputStream = new OutputStreamWriter(socketConnection
                    .getOutputStream());
            BufferedWriter bufferedOutput = new BufferedWriter(outputStream);
            PrintWriter printWriter = new PrintWriter(bufferedOutput, true);

            String clientCommand = "";
            String responseQuery = "";
            String queryInput = "";

            try {
                Connection conn = null;
                Statement stmt = null;
                Class.forName("org.postgresql.Driver");
                conn = DriverManager
                        .getConnection("jdbc:postgresql://localhost:5432/train_reservation_system",
                                "postgres", "shyam2672002");
                System.out.println("Opened database successfully");

                stmt = conn.createStatement();

                // stmt.executeQuery("ALTER DATABASE train_reservation_system SET
                // DEFAULT_TRANSACTION_ISOLATION TO 'serializable';");
                // conn.setTransactionIsolation(8);t66

                while (true) {
                    // Read client query
                    clientCommand = bufferedInput.readLine();
                    // System.out.println("Recieved data <" + clientCommand + "> from client : "
                    // + socketConnection.getRemoteSocketAddress().toString());

                    // Tokenize here
                    StringTokenizer tokenizer = new StringTokenizer(clientCommand);

                    String q = "call BOOKTICKET(";
                    int f = 0;
                    int nofp = 0;
                    String train_id = "";
                    String doj = "";
                    String nofpp = "";
                    String type = "";
                    int nump = 0;
                    // System.out.println(clientCommand + " ");

                    // while(tokenizer.hasMoreTokens()){
                    // queryInput = tokenizer.nextToken();
                    // System.out.println(queryInput + " ");
                    // }

                    while (tokenizer.hasMoreTokens()) {
                        queryInput = tokenizer.nextToken();
                        // System.out.println(queryInput + " ");
                        // System.out.println(nofp);

                        if (queryInput.charAt(0) == '#') {

                            String returnMsg = "Connection Terminated - client : "
                                    + socketConnection.getRemoteSocketAddress().toString();
                            System.out.println(returnMsg);
                            inputStream.close();
                            bufferedInput.close();
                            outputStream.close();
                            bufferedOutput.close();
                            printWriter.close();
                            socketConnection.close();
                            stmt.close();
                            conn.close();
                            return;
                        } else if (f == 0) {
                            nofpp = queryInput;
                            nofp = atoi(nofpp) + 1;
                            nump = atoi(nofpp);
                            q = q + nofpp + ",'{";
                        } else if (nofp > 1) {

                            q = q + queryInput;
                        } else if (nofp == 1) {
                            q = q + queryInput + "}',";
                        } else if (nofp == 0) {
                            train_id = queryInput;
                            q = q + queryInput + ',';
                        } else if (nofp == -1) {
                            doj = "'" + queryInput + "'";
                            q = q + "'" + queryInput + "',";

                        }

                        else if (nofp == -2) {
                            type = queryInput;
                            q = q + "'" + queryInput + "',";
                        }

                        f = f + 1;
                        nofp = nofp - 1;
                    }

                    // System.out.println(q);
                    // System.out.println("Opened database successfully");
                    String check_train_availibilty = "select * from train where train.train_id=" + train_id + " AND "
                            + "train.doj=" + doj + ";";
                    System.out.println(check_train_availibilty);
                    ResultSet rs;
                    ResultSetMetaData rsmd;
                    int columnsNumber;
                    String s = "";
                    try {

                        rs = stmt.executeQuery(check_train_availibilty);
                        rsmd = rs.getMetaData();
                        columnsNumber = rsmd.getColumnCount();
                        String s1 = "";
                        while (rs.next()) {
                            for (int i = 1; i <= columnsNumber; i++) {

                                s1 = s1 + " ";
                                String columnValue = rs.getString(i);
                                // System.out.print(columnValue + " " + rsmd.getColumnName(i));
                                s1 += columnValue;
                            }
                            s1 = s1 + "\n";

                        }
                        System.out.println(s);
                        if (s1 == "") {
                            // System.out.println(train_id + "Sorry train not available for this date");

                            printWriter.println(train_id + " Sorry train not available for this date");
                            continue;
                        }

                    } catch (Exception e) {
                        System.out.println(train_id + "Sorry train not available for this date");

                        printWriter.println(train_id + " Sorry train not available for this date");
                        continue;
                        // TODO: handle exception
                    }
                    type = func(type);
                    String check_seat_availibilty = " select count(doj) as count from TRAIN_POOL where TRAIN_POOL.train_id="
                            + train_id + "and TRAIN_POOL.doj=" + doj

                            + "and TRAIN_POOL.empty=1 and TRAIN_POOL.COACH_TYPE=" + type + " GROUP BY (train_id );";
                    String noa = "";
                    try {

                        rs = stmt.executeQuery(check_seat_availibilty);
                        rsmd = rs.getMetaData();
                        columnsNumber = rsmd.getColumnCount();
                        while (rs.next()) {
                            for (int i = 1; i <= columnsNumber; i++) {

                                // s = s + " ";
                                String columnValue = rs.getString(i);
                                // System.out.print(columnValue + " " + rsmd.getColumnName(i));
                                s += columnValue;
                            }

                        }
                        noa = s;
                        // System.out.println(noa + "fff");

                    } catch (Exception e) {
                        // System.out.println(noa + "fff" + "ggg");
                        System.out.println(e);

                        printWriter
                                .println(train_id + " Sorry the requested number of seats not available for this date");
                        continue;
                        // TODO: handle exception
                    }
                    System.out.println(noa);
                    System.out.println(atoi("108"));
                    int n1 = atoi(noa), n2 = atoi(nofpp);
                    System.out.println(n1);
                    System.out.println(n2);

                    if (n1 < n2) {
                        printWriter.println(" Sorry the requested number of seats not available for this date");
                        continue;
                    }
                    String pnr = "";
                    s = "";
                    while (true) {
                        pnr = generatepnr();
                        String check = "Select* from TICKET where TICKET.pnr=" + pnr + ";";

                        try {
                            rs = stmt.executeQuery(q);
                            rsmd = rs.getMetaData();
                            columnsNumber = rsmd.getColumnCount();
                            while (rs.next()) {
                                for (int i = 1; i <= columnsNumber; i++) {

                                    // s = s + " ";
                                    String columnValue = rs.getString(i);
                                    // System.out.print(columnValue + " " + rsmd.getColumnName(i));
                                    s += columnValue;
                                }
                                // s = s + "\n";

                            }
                            if (s == "") {
                                break;
                            }
                        } catch (Exception e) {
                            break;
                            // TODO: handle exception
                        }

                    }

                    try {
                        q = q + pnr + ");";
                        System.out.println(q);
                        stmt.executeQuery(q);

                    } catch (Exception e) {

                        // TODO: handle exception
                    }

                    try {
                        responseQuery = "";

                        q = "Select* from TICKET where TICKET.pnr=" + pnr + ";";
                        System.out.println(q);

                        rs = stmt.executeQuery(q);
                        rsmd = rs.getMetaData();
                        columnsNumber = rsmd.getColumnCount();
                        while (rs.next()) {
                            for (int i = 1; i <= columnsNumber; i++) {
                                if (i == 1)
                                    responseQuery += "Name ---> ";
                                if (i == 2)
                                    responseQuery += "      Train No ---> ";
                                if (i == 3)
                                    responseQuery += "      Date Of Journey ---> ";
                                if (i == 4)
                                    responseQuery += "      PNR ---> ";
                                if (i == 5)
                                    responseQuery += "      Coach No ---> ";
                                if (i == 6)
                                    responseQuery += "      Coach Type ---> ";
                                if (i == 7)
                                    responseQuery += "      Berth No ---> ";
                                if (i == 8)
                                    responseQuery += "      Berth Type ---> ";
                                if (i > 1)
                                    responseQuery = responseQuery + " ";
                                String columnValue = rs.getString(i);
                                // System.out.print(columnValue + " " + rsmd.getColumnName(i));
                                responseQuery += columnValue;
                            }
                            responseQuery = responseQuery + "\n";

                        }
                        System.out.println(responseQuery);
                        printWriter.println(responseQuery);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                        System.exit(0);
                        // TODO: handle exception
                    }

                    // responseQuery = "";

                    // -------------- your DB code goes here----------------------------
                    // try
                    // {
                    // Thread.sleep(6000);
                    // }
                    // catch (InterruptedException e)
                    // {
                    // e.printStackTrace();
                    // }

                    // responseQuery = "******* Dummy result ******";

                    // ----------------------------------------------------------------

                    // Sending data back to the client
                    // System.out.println("\nSent results to client - "
                    // + socketConnection.getRemoteSocketAddress().toString() );

                }

            }

            catch (Exception e) {
                // TODO: handle exception
                System.out.println(e.getMessage());

            }

        } catch (IOException e) {
            return;
        }
    }
}

/**
 * Main Class to controll the program flow
 */
public class ServiceModule {
    // Server listens to port
    static int serverPort = 7005;
    // Max no of parallel requests the server can process
    static int numServerCores = 5;

    // ------------ Main----------------------
    public static void main(String[] args) throws IOException {
        // Creating a thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(numServerCores);

        try (// Creating a server socket to listen for clients
                ServerSocket serverSocket = new ServerSocket(serverPort)) {
            Socket socketConnection = null;

            // Always-ON server
            while (true) {
                System.out.println("Listening port : " + serverPort
                        + "\nWaiting for clients...");
                socketConnection = serverSocket.accept(); // Accept a connection from a client
                System.out.println("Accepted client :"
                        + socketConnection.getRemoteSocketAddress().toString()
                        + "\n");
                // Create a runnable task
                Runnable runnableTask = new QueryRunner(socketConnection);
                // Submit task for execution
                executorService.submit(runnableTask);
            }
        }
    }
}
