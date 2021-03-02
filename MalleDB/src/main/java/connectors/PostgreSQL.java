package connectors;

import interfaces.SubDB;
import org.postgresql.Driver;
import util.Item;
import util.Options;
import util.Status;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
    private int ROW = 0;
    private int COL = 0;

    public Status init() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            conn = DriverManager.getConnection(url, user, password);
            return Status.OK;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
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

    @Override
    public String[][] select(String query) {
        try {
            conn.setCatalog(Options.DB_POST);// DB 변경
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(query);
            rs.last();             //DB 마지막레코드로 이동
            ROW = rs.getRow(); //레코드 개수 반환
            rs.beforeFirst();      //처음 상태로복귀
            ResultSetMetaData md = rs.getMetaData();//
            COL = md.getColumnCount();//컬럼 반환

            String[][] data = new String[ROW][COL];
            int cur = 1;
            for (int row = 0; row < ROW; row++) {
                rs.next();
                for (int col = 0; col < COL; col++) {
                    System.out.println(ROW +" "+ COL +"set: " + col+1 + "\n");
                    if(md.getColumnType(col)==6) {  // int
                        data[row][col] = Integer.toString(rs.getInt((col + 1)));
                    }
                    else if(md.getColumnType(col)==1) { // String
                        data[row][col] = rs.getString((col + 1));
                    }
                    else if(md.getColumnType(col)==9) { // Double
                        data[row][col] = Double.toString(rs.getDouble((col + 1)));
                    }
                    else if(md.getColumnType(col)==10) {    // Float
                        data[row][col] = Float.toString(rs.getFloat((col + 1)));
                    }
                    //develop efficiency
                    System.out.println(data[row][col]+"\n");
                }
            }
            return data;

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
        }
    }

    @Override
    public Status execute(String query) {
        // Type: 1=Create, 2= Insert 3= Update
        try {//타입 감지해서 나눌것
            conn.setCatalog(Options.DB_POST);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            if (query.startsWith("S")) {
                rs = stmt.executeQuery(query);//select용
            } else {
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

    @Override
    public Status flush_query(String[] query) {
        try {
            conn.setCatalog(Options.DB_POST);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < query.length; i++) {
                stmt.addBatch(query[i]);
            }
            stmt.executeBatch();
            stmt.clearBatch();
            return Status.OK;
        } catch (SQLException ex) {
            return Status.ERROR;
        }
    }


    @Override
    public Status insert(Item item) {
        try {
            if (item.isMeta()) {
                String query = "INSERT INTO " + Options.TABLE_META_POSTGRESQL + " (d_key, m_count, b_count, t_count) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, item.getKey());
                pstmt.setInt(2, item.getCounters()[0]);
                pstmt.setInt(3, item.getCounters()[1]);
                pstmt.setInt(4, item.getCounters()[2]);
                pstmt.execute();
                System.out.println("Metadata for key \"" + item.getKey() + "\" inserted...");

                pstmt.close();
            } else {
                String table = Options.TABLES_POSTGRESQL[item.getType() - 1];

                String query = "INSERT INTO " +  table + " (d_order, d_key, d_value) VALUES (?, ?, ?)";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, item.getOrder());
                pstmt.setString(2, item.getKey());
                pstmt.setString(3, new String(escapeString(item.getValue()).getBytes()));
                //System.out.println("Table: " + table);
                System.out.println("Inserting: " + item.toStringBlock());
                pstmt.execute();
                System.out.println("Item \"" + item.getKey() + "\" inserted...");

                pstmt.close();
            }
            return Status.OK;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Status.ERROR;
        }
    }

    @Override
    public Item readMeta(Item item) {
        try {
            String key = item.getKey();
            String query = "SELECT * FROM " + Options.TABLE_META_MYSQL + " WHERE d_key='" + key + "';";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                int[] counters = new int[3];
                counters[0] = rs.getInt("m_count");
                counters[1] = rs.getInt("b_count");
                counters[2] = rs.getInt("t_count");
                item.setCounters(counters);
                System.out.println("Item \"" + key + "\" is retrieved...");
                return item;
            } else {
                System.out.println("Item \"" + key + "\" is not found...");
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Item> readAll(String table, Item item) {
        try {
            List<Item> items = new ArrayList<>();
            String key = item.getKey();
            String query = "SELECT * FROM " + table + " WHERE d_key='" + key + "';";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int order = rs.getInt("d_order");
                String value = new String(rs.getString("d_value"));
                items.add(new Item(order, 0, key, value));
            }

            return sortByOrder(items);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Status delete(String table, Item item) {
        try {
            stmt = conn.createStatement();
            String query = "DELETE FROM " + table + " WHERE d_key='" + item.getKey() + "';";
            System.out.println("Query: " + query);
            stmt.executeUpdate(query);
            System.out.println("Item for key \"" + item.getKey() + "\" deleted...");
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private String escapeString(String s) {
        return s.replaceAll("\b", "\\b")
                .replaceAll("\n", "\\n")
                .replaceAll("\r", "\\r")
                .replaceAll("\t", "\\t")
                .replaceAll("\\x1A", "\\Z")
                .replaceAll("\\x00", "\\0")
                .replaceAll("'", "\\'")
                .replaceAll("\"", "\\\"");
    }

    public Status close() {
        try {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (pstmt != null)
                pstmt.close();
            if (conn != null)
                conn.close();
            return Status.OK;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Status.ERROR;
        }
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
