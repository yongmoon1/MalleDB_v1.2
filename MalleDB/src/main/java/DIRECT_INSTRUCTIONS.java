import db.MalleDB;
import util.Options;

public class DIRECT_INSTRUCTIONS {
    public static void main(String[] args) {
        MalleDB malleDB = new MalleDB();
        Options options_redis = new Options(Options.DB_TYPE.REDIS);
        Options options_leveldb = new Options(Options.DB_TYPE.LEVELDB);

        //malleDB.init(options_redis);
        malleDB.init(options_leveldb);

        malleDB.direct_insert("WHAT", "ME");
        malleDB.direct_read("WHAT");
        malleDB.direct_update("WHAT", "YOU");
        malleDB.direct_read("WHAT");
        malleDB.direct_delete("WHAT");
    }
}
