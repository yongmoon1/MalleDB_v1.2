import db.MalleDB;
import file.FileManager;
import util.Options;

import java.io.File;
import java.io.IOException;

public class fileManage {
    public static void main(String[] args) throws IOException {
        MalleDB malleDB = new MalleDB();
        malleDB.init(new Options(Options.DB_TYPE.REDIS));

        FileManager fileManager = new FileManager(malleDB);
        //fileManager.insertFile("C:\\Users\\ChulWoo_Lee\\Desktop\\algorithm.png");
        fileManager.readFile("20210427180338algorithm.png");
    }
}
