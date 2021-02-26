package connectors;

import interfaces.SubDB;
import util.Item;
import util.Status;
import util.Options;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MySQL extends SubDB {

    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;
    private String[] querytype = {"create", "insert", "update"};
    private int ROW = 0;
    private int COL = 0;


    @Override
    public Status init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(Options.SERVER_MYSQL +
                    "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&user=" +
                    Options.USER_MYSQL + "&password=" + Options.PASSW_MYSQL);
            //conn = DriverManager.getConnection("jdbc:mysql://localhost/?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&user=iesl&password=12345678");
            System.out.println("Successfully Connected to MYSQL Server...");
            return Status.OK;

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return Status.ERROR;
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
            return Status.ERROR;
        }
    }

    @Override
    public Status create() {
        try {
            stmt = conn.createStatement();

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + Options.DB_MYSQL + "." + Options.TABLE_META_MYSQL + "(\n" +
                    "        id INT NOT NULL AUTO_INCREMENT,\n" +
                    "        d_key VARCHAR(100) NOT NULL,\n" +
                    "        m_count int NOT NULL,\n" +
                    "        b_count int NOT NULL,\n" +
                    "        t_count int NOT NULL,\n" +
                    "        PRIMARY KEY (ID)\n" +
                    "        );");
            System.out.println("Successfully created table \"" + Options.TABLE_META_MYSQL + "\"...");

            if (Options.SUB_DB == Options.DB_TYPE.MYSQL) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + Options.DB_MYSQL + "." + Options.TABLE_MDATA_MYSQL + "(\n" +
                        "        id INT NOT NULL AUTO_INCREMENT,\n" +
                        "        d_order int NOT NULL,\n" +
                        "        d_key VARCHAR(100) NOT NULL,\n" +
                        "        d_value MEDIUMBLOB NOT NULL,\n" +
                        "        PRIMARY KEY (ID)\n" +
                        "        );");
                System.out.println("Successfully created table \"" + Options.TABLE_MDATA_MYSQL + "\"...");

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + Options.DB_MYSQL + "." + Options.TABLE_BDATA_MYSQL + "(\n" +
                        "        id INT NOT NULL AUTO_INCREMENT,\n" +
                        "        d_order int NOT NULL,\n" +
                        "        d_key VARCHAR(100) NOT NULL,\n" +
                        "        d_value BLOB NOT NULL,\n" +
                        "        PRIMARY KEY (ID)\n" +
                        "        );");
                System.out.println("Successfully created table \"" + Options.TABLE_BDATA_MYSQL + "\"...");

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + Options.DB_MYSQL + "." + Options.TABLE_TDATA_MYSQL + "(\n" +
                        "        id INT NOT NULL AUTO_INCREMENT,\n" +
                        "        d_order int NOT NULL,\n" +
                        "        d_key VARCHAR(100) NOT NULL,\n" +
                        "        d_value TINYBLOB NOT NULL,\n" +
                        "        PRIMARY KEY (ID)\n" +
                        "        );");
                System.out.println("Successfully created table \"" + Options.TABLE_TDATA_MYSQL + "\"...");
            }
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

    @Override
    public Status insert(Item item) {
        try {
            if (item.isMeta()) {
                String query = "INSERT INTO " + Options.DB_MYSQL + "." + Options.TABLE_META_MYSQL + " (d_key, m_count, b_count, t_count) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, item.getKey());
                pstmt.setInt(2, item.getCounters()[0]);
                pstmt.setInt(3, item.getCounters()[1]);
                pstmt.setInt(4, item.getCounters()[2]);
                pstmt.execute();
                System.out.println("Metadata for key \"" + item.getKey() + "\" inserted...");

                pstmt.close();
            } else {
                String table = Options.TABLES_MYSQL[item.getType() - 1];

                String query = "INSERT INTO " + Options.DB_MYSQL + "." + table + " (d_order, d_key, d_value) VALUES (?, ?, ?)";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, item.getOrder());
                pstmt.setString(2, item.getKey());
                pstmt.setBlob(3, new ByteArrayInputStream(escapeString(item.getValue()).getBytes()));
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
            String query = "SELECT * FROM " + Options.DB_MYSQL + "." + Options.TABLE_META_MYSQL + " WHERE d_key='" + key + "';";
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
            String query = "SELECT * FROM " + Options.DB_MYSQL + "." + table + " WHERE d_key='" + key + "';";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int order = rs.getInt("d_order");
                String value = new String(rs.getBlob("d_value").getBytes(1l, (int) rs.getBlob("d_value").length()));
                items.add(new Item(order, 0, key, value));
            }

            return sortByOrder(items);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public String[][] select(String query) {

        try {
            conn.setCatalog(Options.DB_MYSQL);// DB 변경
            stmt = conn.createStatement();
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
            conn.setCatalog(Options.DB_MYSQL);
            stmt = conn.createStatement();
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
            conn.setCatalog(Options.DB_MYSQL);
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
    public Status update(String table, Item item) {
        return null;
    }

    @Override
    public Status delete(String table, Item item) {
        try {
            stmt = conn.createStatement();
            String query = "DELETE FROM " + Options.DB_MYSQL + "." + table + " WHERE d_key='" + item.getKey() + "';";
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

    public int getROW() {
        return ROW;
    }

    public int getCOL() {
        return COL;
    }

}
