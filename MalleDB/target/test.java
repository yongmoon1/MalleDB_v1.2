import db.MalleDB;
import util.Options;

public class test {
    public static void main(String[] args){
        MalleDB malleDB = new MalleDB();
        malleDB.init(new Options(Options.DB_TYPE.LEVELDB));

        malleDB.insert("filename", "1");
        malleDB.read("filename");
    }
}
