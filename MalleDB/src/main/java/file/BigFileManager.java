package file;

import db.MalleDB;
import util.MetaFile;
import util.Options;
import util.Status;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class BigFileManager {

    MalleDB malleDB;

    public BigFileManager(MalleDB malleDB) {
        this.malleDB = malleDB;
    }

    public void bigFileInsert(String filepath) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(filepath, "r");
        int sourceSize = Long.valueOf(raf.length()).intValue();
        int chunkCount = sourceSize / Options.BUFFERSIZE + 1;
        int remainingBytes = sourceSize % Options.BUFFERSIZE;
        String fileName = getFileName(filepath);

        // Insert MetaFile for BigFile
        MetaFile metaFile = new MetaFile(sourceSize, fileName, true, chunkCount);
        malleDB.insert(metaFile.getid(), metaFile.toString());

        //test
        String testMeta = metaFile.toString();
        System.out.println(testMeta);
        MetaFile newMeta = new MetaFile();
        newMeta.Stringto(testMeta);

        for (int chunkNum = 1; chunkNum <= chunkCount; chunkNum++) {
            byte[] buf;
            if (chunkNum != chunkCount) {
                buf = new byte[(int) Options.BUFFERSIZE];
            } else {
                buf = new byte[(int) remainingBytes];
            }
            int val = raf.read(buf);
            malleDB.insert(metaFile.getid() + chunkNum, buf.toString()); // key: filename + chunkNum
        }
        raf.close();
    }

    public void bigFileRead(String metaID) {
        String metaFileString = malleDB.read(metaID).getValue();
        MetaFile metaFile = new MetaFile();
        metaFile.Stringto(metaFileString);
        int chunkCount = metaFile.getN();
        for (int chunkNum = 1; chunkNum <= chunkCount; chunkNum++) {
            String chunk = malleDB.read(metaID + chunkNum).getValue();
            RandomAccessFile raf = new RandomAccessFile()
        }
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
