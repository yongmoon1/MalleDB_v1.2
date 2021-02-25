package connectors;

import interfaces.SubDB;
import util.Options;
import util.Status;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQL extends SubDB {

    private static String url = "jdbc:postgresql://localhost:5432/" + Options.DB_POST;
    private static String user = "postgres";
    private static String password = "12345678";
    private static Connection conn;
    private static ResultSet rs = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    private int ROW=0;
    private int COL=0;

    public Status init() {
        try {
            conn = DriverManager.getConnection(url, user, password);
            return Status.OK;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Status.ERROR;
        }
    }

    @Override
    public Status create() {
        try {
            stmt = conn.createStatement();

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + Options.TABLE_META_POSTGRESQL + "(\n" +
                    "        id SERIAL NOT NULL,\n" +
                    "        d_key VARCHAR(100) NOT NULL,\n" +
                    "        m_count int NOT NULL,\n" +
                    "        b_count int NOT NULL,\n" +
                    "        t_count int NOT NULL,\n" +
                    "        PRIMARY KEY (ID)\n" +
                    "        );");
            System.out.println("Successfully created table \"" + Options.TABLE_META_POSTGRESQL + "\"...");


            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + Options.TABLE_MDATA_POST + "(\n" +
                    "        id SERIAL NOT NULL,\n" +
                    "        d_order int NOT NULL,\n" +
                    "        d_key VARCHAR(100) NOT NULL,\n" +
                    "        d_value TEXT NOT NULL,\n" +
                    "        PRIMARY KEY (ID)\n" +
                    "        );");
            System.out.println("Successfully created table \"" + Options.TABLE_MDATA_POST + "\"...");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + Options.TABLE_BDATA_POST + "(\n" +
                    "        id SERIAL NOT NULL,\n" +
                    "        d_order int NOT NULL,\n" +
                    "        d_key VARCHAR(100) NOT NULL,\n" +
                    "        d_value TEXT NOT NULL,\n" +
                    "        PRIMARY KEY (ID)\n" +
                    "        );");
            System.out.println("Successfully created table \"" + Options.TABLE_BDATA_POST + "\"...");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + Options.TABLE_TDATA_POST + "(\n" +
                    "        id SERIAL NOT NULL,\n" +
                    "        d_order int NOT NULL,\n" +
                    "        d_key VARCHAR(100) NOT NULL,\n" +
                    "        d_value TEXT NOT NULL,\n" +
                    "        PRIMARY KEY (ID)\n" +
                    "        );");
            System.out.println("Successfully created table \"" + Options.TABLE_TDATA_POST + "\"...");
            stmt.close();
            return Status.OK;

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return Status.ERROR;
        }
    }

    public String[][] select(String DBName, String query) {
        try {
            conn.setCatalog(DBName);// DB 변경
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(query);
            rs.last();             //DB 마지막레코드로 이동
            ROW = rs.getRow(); //레코드 개수 반환
            rs.beforeFirst();      //처음 상태로복귀
            ResultSetMetaData md = rs.getMetaData();//
            COL = md.getColumnCount();//컬럼 반환

            String[][] data = new String[ROW][COL];
            int cur = 1;
            for(int row = 0; row < ROW; row++ ) {
                rs.next();
                for (int col = 0; col < COL; col++) {
                    System.out.println(ROW +" "+ COL +"set: " + col+1 + "\n");
                    data[row][col] = Integer.toString(rs.getInt((col+1)));//추후 컬럼별 타입별로 수정
                    System.out.println(data[row][col]+"\n");
                }

            }System.out.println("done");
            return data;

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
        }
    }

    public Status execute(String DBName,String query) {
        // Type: 1=Create, 2= Insert 3= Update
        try{//타입 감지해서 나눌것
            conn.setCatalog(DBName);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            if(query.startsWith("S")) {
                rs = stmt.executeQuery(query);//select용
            }
            else {
                stmt.executeUpdate(query);//create insert delete 용
            }
            stmt.close();
            return Status.OK;
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return Status.ERROR;
        }
        // 타입 별 쿼리 실행

    }

    public Status close() {

        return Status.OK;
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }

    public static Connection getCon() {
        return conn;
    }

    public static void setUrl(String url) {
        PostgreSQL.url = url;
    }

    public static void setUser(String user) {
        PostgreSQL.user = user;
    }

    public static void setPassword(String password) {
        PostgreSQL.password = password;
    }

    public static void setCon(Connection conn) {
        PostgreSQL.conn = conn;
    }
}
