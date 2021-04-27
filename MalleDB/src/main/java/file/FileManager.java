package file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import db.MalleDB;
import util.*;
//test pull
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileManager {
    private final MalleDB malleDB;
    private final SmallFileManager smallFileManager;
    private final BigFileManager bigFileManager;
    private String prefix = "123456";

    public boolean isBig(String filePath) {
        int size;
        File file = new File(filePath);
        if (file.isFile()) {
            size = Long.valueOf(file.length()).intValue();
            if (size > Options.BUFFER_SIZE) return true;
            else return false;
        } else {
            System.out.println("WRONG FILE PATH");
            return false;
        }
    }

    public FileManager(MalleDB malleDB) {
        this.malleDB = malleDB;
        smallFileManager = new SmallFileManager(malleDB);
        bigFileManager = new BigFileManager(malleDB);
    }

    public Status insertMetaFile(MetaFile newmeta) {
        String key;
        String metaInfo;
        metaInfo = newmeta.toString();
        key = prefix + newmeta.getKey();//change random generate key
        return malleDB.insert(key, metaInfo);
    }

    public Status updateMetaFile(String key, MetaFile newmeta) {
        //뉴 메타 파일의 정보에서 기존 메타파일과 동일점 찾아서 업데이트
        deleteMetaFile(key);
        String metaInfo = newmeta.toString();
        return malleDB.insert(key, metaInfo);
    }

    public void deleteMetaFile(String key) {
        malleDB.delete(key);
    }

    public MetaFile readMetaFile(String key) {
        String valueByteArray = malleDB.read(key).getValue();
        String value = new String(valueByteArray);
        MetaFile metaFile = new MetaFile();
        metaFile.Stringto(value);
        return metaFile;
    }

    public Status insertFile(String filename) throws IOException {
        System.out.println("Inserting File : " + filename);

        File file = new File(filename);

        if (isBig(filename)) {
            bigFileManager.bigFileInsert(filename);
        } else {
            smallFileManager.smallFileInsertEncoder(filename);
        }
        return Status.OK;
    }

    public Status readFile(String metaID) throws IOException {
        String metaFileString = malleDB.read(metaID).getValue();
        MetaFile metaFile = new MetaFile();
        metaFile.Stringto(metaFileString);

        if (metaFile.isBig()) {
            bigFileManager.bigFileRead(metaFile);
        } else {
            //smallFileManager.smallFileDataRead(metaFile);
        }
        return Status.OK;
    }

    public Status updateFile(String metaID) {
        String metaFileString = malleDB.read(metaID).getValue();
        MetaFile metaFile = new MetaFile();
        metaFile.Stringto(metaFileString);

        if (metaFile.isBig()) {
            bigFileManager.bigFileUpdate(metaFile);
        } else {
            // Small File Update
        }
        return Status.OK;
    }

    public Status deleteFile(String metaID) {
        String metaFileString = malleDB.read(metaID).getValue();
        MetaFile metaFile = new MetaFile();
        metaFile.Stringto(metaFileString);

        if (metaFile.isBig()) {
            bigFileManager.bigFileDelete(metaFile);
        } else {
            // Small File Update
        }
        return Status.OK;
    }

    public static String encoder(byte[] imageData) {
        System.out.println("ENCODING");
        return Base64.getEncoder().encodeToString(imageData);
    }

    public static byte[] decoder(String base64Image) {
        return Base64.getDecoder().decode(base64Image);
    }

}
