import db.MalleDB;
import db.FileManager;
import util.Options;

import java.io.IOException;

public class fileManage {
    public static void main(String[] args) throws IOException {
        MalleDB malleDB = new MalleDB();
        malleDB.init(new Options(Options.DB_TYPE.REDIS));

        FileManager fileManager = new FileManager(malleDB);
        fileManager.insertFile("C:\\Users\\ChulWoo\\Desktop\\malltest\\image.png");

    }
}
