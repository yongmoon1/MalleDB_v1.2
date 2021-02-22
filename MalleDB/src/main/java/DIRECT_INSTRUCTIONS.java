import db.MalleDB;
import util.Options;

public class DIRECT_INSTRUCTIONS {
    public static void main(String[] args) {
        MalleDB malleDB = new MalleDB();
        //Options options_redis = new Options(Options.DB_TYPE.REDIS);
        //Options options_leveldb = new Options(Options.DB_TYPE.LEVELDB);
        //Options options_mysql = new Options(Options.DB_TYPE.MYSQL);
        Options options_cass = new Options(Options.DB_TYPE.CASSANDRA);

        //malleDB.init(options_redis);
        //malleDB.init(options_leveldb);
        //malleDB.init(options_mysql);
        malleDB.init(options_cass);

        malleDB.create();
        malleDB.direct_insert("WHAT", "ME");
        malleDB.direct_read("WHAT");
        malleDB.direct_update("WHAT", "YOU");
        malleDB.direct_read("WHAT");
        malleDB.direct_delete("WHAT");
//        malleDB.direct_create();
//        malleDB.direct_insert("key1", "value1");
//        malleDB.direct_read("key1");
//        malleDB.direct_update("key1", "value2");
//        malleDB.direct_read("key1");
//        malleDB.direct_delete("key1");
//        malleDB.direct_read("key1");
        malleDB.close();
    }
}
