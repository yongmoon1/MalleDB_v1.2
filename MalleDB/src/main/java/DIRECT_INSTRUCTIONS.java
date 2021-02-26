import db.MalleDB;
import util.Options;

public class DIRECT_INSTRUCTIONS {

    public static String[][] Result;

    public static void main(String[] args) {
        MalleDB malleDB = new MalleDB();
        //Options options_redis = new Options(Options.DB_TYPE.REDIS);
        //Options options_leveldb = new Options(Options.DB_TYPE.LEVELDB);
        //Options options_mysql = new Options(Options.DB_TYPE.MYSQL);
        //Options options_cass = new Options(Options.DB_TYPE.CASSANDRA);
        Options option_post = new Options(Options.DB_TYPE.POSTGRESQL);

        //malleDB.init(options_redis);
        //malleDB.init(options_leveldb);
        //malleDB.init(options_mysql);
        //malleDB.init(options_cass);
        malleDB.init(option_post);

        //malleDB.insert("key1", "value1");
        //malleDB.read("key1");
        //malleDB.create();
          //malleDB.delete("key1");
//        malleDB.direct_insert("WHAT", "ME");
//        malleDB.direct_read("WHAT");
//        malleDB.direct_update("WHAT", "YOU");
//        malleDB.direct_read("WHAT");
//        malleDB.direct_delete("WHAT");
//
//        malleDB.select("select * from tdatatable");
        //malleDB.execute("INSERT INTO tdatatable(d_order, d_key, d_value) values(20, \'abc\', " +
        //        "\'123abc\')");
        malleDB.flush_query(new String[]{
               // "DELETE from tdatatable where d_order = 1234",
                "INSERT INTO tdatatable(d_order, d_key, d_value) values(20, \'abc\', \'123abc\')",
                "INSERT INTO tdatatable(d_order, d_key, d_value) values(30, \'abc\', \'123abc\')"
        });
//        Result = malleDB.select("select * from tdatatable");
//
//        int Row = Result.length;
//        int Col = Result[0].length;
//
//        for(int i = 0; i<Row;i++) {
//            for (int j = 0; j < Col; j++) {
//                System.out.println(Result[i][j] + ' ');
//            }
//            System.out.println('\n');
//        }
    }
}
