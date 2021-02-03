import db.MalleDB;

public class test {
    public static void main(String[] args){
        MalleDB malleDB = new MalleDB();
        malleDB.init();
        malleDB.create();

        malleDB.insert("filename", "1");
        malleDB.read("filename");
    }
}
