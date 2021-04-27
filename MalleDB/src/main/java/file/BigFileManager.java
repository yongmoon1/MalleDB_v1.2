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
            //RandomAccessFile raf = new RandomAccessFile();
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

    public void BigfileinsertAPI(MetaFile metaFile, byte[] data, int seq) throws IOException{// not test
             if(seq == 1){
                 malleDB.insert(metaFile.getid(), metaFile.toString());
             }
        //이름 정하기 이 청크의 크기는 거의 메모리급의 크기를 지닐것으로 가정. 이 청크는 bigfilemanager의 기능에 의해 버퍼 사이즈만큼쪼개져 들어갈것
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[Options.BUFFERSIZE];
        int chunkNum = 1;
        while(inputStream.read(buffer) != -1){
            //meta id 형식은 = metaid+chunknumber+_+seq= metaid6_2  2번 seq 파일의 6번째 버퍼사이즈 파일
            malleDB.insert(metaFile.getid() + chunkNum+"_"+seq, buffer.toString());
            chunkNum++;
        }

    }

    public void BigFileDelete(MetaFile metaFile){// not test
        //MetaFile에 API 여부를 추가해야할듯
        Item tm = malleDB.readKV(metaFile.getid());
        MetaFile DMF= new MetaFile();      //DMF = deleteMetaFile
        DMF.Stringto(tm.getValue());
        if(DMF.isAPI()){
           for(int seq = 1; seq <33; seq++) {//33은 후에 메타파일에 max seq 를 추가해서 그값을 사용한다.
               for (int chunkNum = 1; chunkNum <= DMF.getN(); chunkNum++) {
                   malleDB.delete(metaFile.getid() + chunkNum+"_"+seq);
               }
           }
        }
        else {
            for (int chunkNum = 1; chunkNum <= DMF.getN(); chunkNum++) {
                malleDB.delete(metaFile.getid() + chunkNum);
            }
        }
        malleDB.delete(metaFile.getid());

    }

}
