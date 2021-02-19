package MalleDB.target;

import db.MalleDB;
import util.Options;

public class test {
    public static void main(String[] args){
        MalleDB malleDB = new MalleDB();
        malleDB.init(new Options(Options.DB_TYPE.LEVELDB, Options.DB_TYPE.LEVELDB, Options.DB_TYPE.LEVELDB));

        malleDB.insert("filename", "11231231232132");
        malleDB.read("filename");
    }
}
