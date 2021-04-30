import db.MalleDB;
import file.FileManager;
import util.Item;
import util.MetaFile;
import util.Options;

import java.io.File;
import java.io.IOException;

public class fileManage {
    public static void main(String[] args) throws IOException {
        MalleDB malleDB = new MalleDB();
        malleDB.init(new Options(Options.DB_TYPE.MYSQL));
        MetaFile meta = new MetaFile();
        meta.setid("testforbig");//파일의 ID
        meta.setname("name of test");
        meta.setMetaListId("testone");
        meta.setAPI(true);
        byte[] a = {48,49,50,51,52,53,54,55,56,57};

        //BigFileManager BigFiBigFileManager();



        FileManager fileManager = new FileManager(malleDB);
        //fileManager.insertFile("C:\\rrr");
        fileManager.readFile("Meta_fi2.txt");

    }
}
