package file;

import db.MalleDB;
import util.MetaFile;
import util.Options;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class BigFileManager {

    MalleDB malleDB;

    public BigFileManager(MalleDB malleDB) {
        this.malleDB = malleDB;
    }

    public void bigFileInsert(String filepath) throws IOException {
        File file = new File(filepath);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis, Options.BUFFER_SIZE);

        //RandomAccessFile raf = new RandomAccessFile(filepath, "r");
        int sourceSize = Long.valueOf(file.length()).intValue();
        int chunkCount = sourceSize / Options.BUFFER_SIZE + 1;
        int remainingBytes = sourceSize % Options.BUFFER_SIZE;
        String fileName = getFileName(filepath);

        // Insert MetaFile for BigFile
        MetaFile metaFile = new MetaFile(sourceSize, fileName, true, chunkCount);
        malleDB.insert(metaFile.getid(), metaFile.toString());

        byte[] buf = new byte[Options.BUFFER_SIZE];
        int chunkNum = 1;
        while(bis.read(buf) != -1)
            malleDB.insert(metaFile.getid() + chunkNum++, FileManager.encoder(buf));

        bis.close();
        fis.close();
    }

    public void bigFileRead(MetaFile metaFile) throws IOException {
        String metaID = metaFile.getid();
        int chunkCount = metaFile.getN();

        FileOutputStream fos = new FileOutputStream("./output.png");
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        BufferedImage bi = new BufferedImage(80, 50, BufferedImage.TYPE_INT_RGB);

        for (int chunkNum = 1; chunkNum <= chunkCount; chunkNum++) {
            String chunk = malleDB.read(metaID + chunkNum).getValue();
            bos.write(FileManager.decoder(chunk));
        }

        bos.close();
        fos.close();
    }

    public void bigFileDelete(MetaFile metaFile) {
        String metaID = metaFile.getid();
        int chunkCount = metaFile.getN();

        for (int chunkNum = 1; chunkNum <= chunkCount; chunkNum++) {
            malleDB.delete(metaID + chunkNum);
        }

        malleDB.delete(metaID);
    }

    public void bigFileUpdate(MetaFile metaFile){

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
