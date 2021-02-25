//import connectors.Cassandra;
//import connectors.LevelDB;
import connectors.MySQL;
import interfaces.SubDB;
import util.Item;
import util.Options;
import util.Status;
import util.MetaFile;
import db.MalleDB;

public class newMain{

    public static String[][] Result;

    public static void main(String[] args){

        System.out.println("Block deleted...011");
        MalleDB malleDB = new MalleDB();
        System.out.println("Block deleted...01111");
        Options options = new Options(Options.DB_TYPE.MYSQL);
        System.out.println("Block deleted...01123");
        malleDB.init(options);

        String query = "CREATE TABLE DDD1(\n value1 int NOT NULL,\n value2 int NOT NULL); ";
        String query1 = "DELETE FROM " + "DDD1 " + "WHERE value1=1";
        String query2 = "SELECT * FROM " + "DDD1 ";
        String query3 = "INSERT INTO DDD1 values(1,56)";
        //String query3 = "";
        //String query4 = "";
         malleDB.execute(query);
        System.out.println("Block deleted...02222223");
        malleDB.execute(query3);
        System.out.println("Block deleted...02555555");
        //malleDB.execute(query1);
        System.out.println("Block deleted...0233333333");
        Result = malleDB.select(query2);
        //malle.execute(query3);
        //malle.execute(query4);
        int Row = Result.length;
        int Col = Result[0].length;

        for(int i = 0; i<Row;i++) {
            for (int j = 0; j < Col; j++) {
                System.out.println(Result[i][j]);
            }
        }

        malleDB.close();

    }

}