package file;

import db.MalleDB;
import org.apache.commons.math3.random.RandomGenerator;
import util.Item;
import util.MetaFile;
import util.Options;
import util.Status;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.*;

public class MiddleFileManager {

    MalleDB malleDB;

    public MiddleFileManager(MalleDB malleDB) {
        this.malleDB = malleDB;
    }

    public Status middleFileInsert(String filepath)throws IOException{
        File file = new File(filepath);
        FileInputStream fis = new FileInputStream(file);


        //RandomAccessFile raf = new RandomAccessFile(filepath, "r");
        int sourceSize = Long.valueOf(file.length()).intValue();
        String fileName = getFileName(filepath);

        BufferedInputStream bis = new BufferedInputStream(fis, sourceSize);

        // Insert MetaFile for BigFile
        MetaFile metaFile = new MetaFile(sourceSize, fileName, 1, 1);
        malleDB.insert("Meta_"+metaFile.getid(), metaFile.toString());

        byte[] buf = new byte[sourceSize];
        int chunkNum = 1;
        while(bis.read(buf) != -1)
            malleDB.insert(metaFile.getid() + chunkNum++, FileManager.encoder(buf));

        bis.close();
        fis.close();
        return Status.OK;
    }//파일 사이즈에 따라 버퍼의 길이가 가변됨
    public Status middleFilesInsert(String filepath)throws IOException{
        File file = new File(filepath);
        FileInputStream fis = new FileInputStream(file);


        //RandomAccessFile raf = new RandomAccessFile(filepath, "r");
        int sourceSize = Long.valueOf(file.length()).intValue();
        String fileName = getFileName(filepath);

        BufferedInputStream bis = new BufferedInputStream(fis, sourceSize);

        // Insert MetaFile for BigFile
        MetaFile metaFile = new MetaFile(sourceSize, fileName, 1, 1);
        malleDB.insert("Meta_"+metaFile.getid(), metaFile.toString());

        byte[] buf = new byte[sourceSize];
        int chunkNum = 1;
        while(bis.read(buf) != -1)
            malleDB.insert(metaFile.getid() + chunkNum++, FileManager.encoder(buf));

        bis.close();
        fis.close();
        return Status.OK;
    }//파일 사이즈에 따라 버퍼의 길이가 가변됨
    public void middleFileDelete(MetaFile metaFile){

        String metaID = metaFile.getid();
        int chunkCount = metaFile.getN();

            for (int chunkNum = 1; chunkNum <= chunkCount; chunkNum++) {
                malleDB.delete(metaID + chunkNum);

            }

        malleDB.delete(metaID);
    }

    public String middleFileRead(MetaFile metaFile) {

        Item tempItem = malleDB.readKV(metaFile.getid());
        return tempItem.getValue();
     // 파일 형식에 따라 read 하는 방식으로 구현해야하나 싶음
    }

    public void middleFileUpdate(MetaFile metaFile) {

    }

    static String getFileName(String filePath) {
        int lastSlashIdx = filePath.lastIndexOf('\\');
        if (lastSlashIdx == -1) {
            return filePath;
        } else {
            return filePath.substring(lastSlashIdx + 1);
        }
    }



}
