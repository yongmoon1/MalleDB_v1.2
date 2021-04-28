import db.MalleDB;
import file.FileManager;
import util.Options;

import java.io.File;
import java.io.IOException;

public class fileManage {
    public static void main(String[] args) throws IOException {
        MalleDB malleDB = new MalleDB();
        malleDB.init(new Options(Options.DB_TYPE.MYSQL));

        FileManager fileManager = new FileManager(malleDB);
        fileManager.insertFile("C:\\rrr");
        fileManager.readFile("Meta_fi2.txt");
    }
}
