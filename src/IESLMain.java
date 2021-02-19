
import db.MalleDB;
import util.Options;

public class IESLMain{

    public static void main(String[] args) {
        MalleDB malleDB = new MalleDB();
        Options options = new Options(Options.DB_TYPE.MYSQL);
        malleDB.init();
        malleDB.create();
        malleDB.insert("testkey", "asdfsdf");
        malleDB.read("testkey");
    }
}