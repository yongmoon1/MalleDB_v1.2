package file;

import db.MalleDB;
import util.Options;
import util.Status;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BigFileManager {

    MalleDB malleDB;

    public BigFileManager(MalleDB malleDB){
        this.malleDB = malleDB;
    }

    public void bigFileInsert(String filepath) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(filepath, "r");
        long sourceSize = raf.length();
        long chunkCount = sourceSize / Options.BUFFERSIZE + 1;
        long remainingBytes = sourceSize % Options.BUFFERSIZE;
        String fileName = getFileName(filepath);
        malleDB.insert(fileName + "Meta", Long.toString(chunkCount));
        for (int chunkNum = 1; chunkNum <= chunkCount; chunkNum++) {
            byte[] buf;
            if (chunkNum != chunkCount){
                buf = new byte[(int) Options.BUFFERSIZE];
            }
            else{
                buf = new byte[(int) remainingBytes];
            }
            int val = raf.read(buf);
            malleDB.insert(fileName + chunkNum, buf.toString()); // key: filename + chunkNum
        }
        raf.close();
    }

    public void bigFileRead(String filename){
        Status chunkCntStatus = malleDB.read(filename);
        int chunkCount = Integer.parseInt(chunkCntStatus.getValue());

    }

    static String getFileName(String filePath){
        int lastSlashIdx = filePath.lastIndexOf('\\');
        if(lastSlashIdx==-1){
            return filePath;
        }
        else{
            return filePath.substring(lastSlashIdx + 1);
        }
    }
}
