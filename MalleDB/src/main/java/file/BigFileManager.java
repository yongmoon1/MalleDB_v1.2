package file;

import db.MalleDB;
import org.apache.commons.math3.random.RandomGenerator;
import util.Item;
import util.MetaFile;
import util.Options;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
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
                 //이부분의 키는 metaListId로 해야할수도
             }
        //이름 정하기 이 청크의 크기는 거의 메모리급의 크기를 지닐것으로 가정. 이 청크는 bigfilemanager의 기능에 의해 버퍼 사이즈만큼쪼개져 들어갈것
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[Options.BUFFER_SIZE];
        int chunkNum = 1;
        while(inputStream.read(buffer) != -1){
            //meta id 형식은 = metaid+chunknumber+_+seq= metaid6_2  2번 seq 파일의 6번째 버퍼사이즈 파일
            malleDB.insert(metaFile.getid() + chunkNum+"_"+seq, buffer.toString());
            chunkNum++;
            //메타리스트 형식에 최대 카운터 수를 표시하던가 아니면 키리스트를 그때그때 추가하던가 해야할듯 싶다.
        }

    }//기존의 빅 파일생성을 사용하지 못하는 이유는 처음 파일 자체의 메타파일 생성과정 때문
     // 굳이 seq 변수값을 뒤에 붙일필요는 없을것같다. 빅 파일인서트를 내부에서 사용할것이 아니라면.

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
// 후에 모든 빅 파일을 생성하는 통합 인서트 파일을 제작하고 그 안에 메타파일을 만든후
// api인서트 여부 판단하여 하는것이 좋을듯
//