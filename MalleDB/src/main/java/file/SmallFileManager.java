package file;

import db.MalleDB;

import util.*;
//adfdjklsdfjkl
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class SmallFileManager {

    MalleDB malleDB;

    MetaFile link;

    public SmallFileManager(MalleDB malleDB){
        this.malleDB = malleDB;
    }

    //여러개의 작은 파일들을 버퍼의 크기에 맟추어 모아서 하나의 Item으로 insert 하는 메소드
    //여러개의 작은 파일들의 메타파일들, 그리고 merge될 item의 key를 받아둠
    //현재 이 파일은 메타파일만 다룸. data는 다루지 않음
    public Status smallFileInsert(MetaFile[] metaFiles, String metalistId) {//수정
        String keyList = new String("");

        //init metalist
        Metalist metalist = new Metalist();
        metalist.setkey(metalistId);
/*
        metalist.addlistall(value);
        metalist.makemerge();

        insert(metalistId, metalist.getallvalue());//insert smallfile's metadata
*/
        //insert each metaFile at MalleDB & add each metainfo in metalist's value and make keyList string
        //각 메타파일을 MalleDB에 insert          key : 각 메타파일에 들어있는 작은 파일의 id(key) .  value : metalistId

        //메타리스트에 각 작은 메타파일로부터 받은 메타정보를 저장.
        for (int i = 0; i < metaFiles.length; i++) {
            // insert(metaFiles[i].getid(), metaFiles[i].toString());\
            //is it meta기능 추가
            malleDB.insert(metaFiles[i].getid(), metalistId);

            metalist.addlist(metaFiles[i].toString());
            keyList += metaFiles[i].getid() + "&";

            //key 리스트를   metaFiles[i].getid() + "^"    ->    metalistId + N + "^" 으로 변경해야 할듯
            //키가 포함관계인경우 고려

        }

        // insert keyList and metailst in MalleDB
        malleDB.insert("KLI_" + metalistId, keyList);
        metalist.makemerge();
        malleDB.insert(metalist.getkey(), metalist.getallvalue());

        //최종적으로

        //key : 각 메타파일의 getid          value : 자신의 data가 저장된 메타리스트의 metalistId        (이정표역할)    N개 insert
        //key : "KLI_" + metalistId        value : 키 리스트 (merge된 data 속에서 원하는 정보를 골라낼 key)
        //key : metalistId                 value :  (각 meta파일별 key + "^" + 각 스몰파일 data    ) * N
        //key : "MLI_" + metalistId        value : 메타파일의 정보들 저장. ( 현재는 갱신할려면 비효율적일거 같은데..... 메타파일은 개별로 저장하는게 갱신이 쉬울것 같다. 일단은 미구현)


        return Status.OK;
    }

    public Status smallOneFileInsert( String filepath,byte[] buf,int sourceSize)throws IOException{
        File file = new File(filepath);
        FileInputStream fis = new FileInputStream(file); //현재 이부분이 단지 파일 네임만으로 구현이 되지 않는 상태이다.


        //RandomAccessFile raf = new RandomAccessFile(filepath, "r");
        //int sourceSize = Long.valueOf(file.length()).intValue();
        String fileName = getFileName(filepath);

        BufferedInputStream bis = new BufferedInputStream(fis, sourceSize);

        // Insert MetaFile for BigFile
        MetaFile metaFile = new MetaFile(sourceSize, fileName, 0, 1);
        malleDB.insert("Meta_"+metaFile.getid(), metaFile.toString());

        while(bis.read(buf) != -1)
            malleDB.insert(metaFile.getid(), FileManager.encoder(buf));

        bis.close();
        fis.close();
       return Status.OK;
    }//파일 사이즈에 따라 버퍼의 길이가 가변됨


    //메타리스트는 메타파일 정보의 value를 저장하고 키리스트는 메타파일의 키들을 저장
    // 또한 각각 파일들은 키와 value를 따로저장 이었지만     key : 메타파일   value : 메타리스트 ID로 변경
    //from metalist, export one file's meta
    public String smallOneFileRead(String metaId) {
        Metalist metalist = new Metalist();
        Item tempItem;
        Item metaListItem;
        Item oneMetaItem;

        //export metalist in MalleDB
        System.out.println(".................read 1");
        tempItem = malleDB.readKV(metaId);       //get metalist key      작은 파일의 메타Id로 메타정보를
        MetaFile tempMeta = new MetaFile();
        tempMeta.Stringto(tempItem.getValue());              //읽으려는 스몰 파일의 메타정보를 메타파일로 변환
        System.out.println(".................read 2");
        if(!tempMeta.getMetaListId().equals("null")) {
            metaListItem = malleDB.readKV(tempMeta.getMetaListId());    //get metalist's Item 메타리스트의 아이템을 가져옴
            System.out.println(".................read 3");
            metalist.setkey(metaListItem.getKey());        // 메타리스트로 변환
            System.out.println(".................read 4");
            metalist.setAllvalue(metaListItem.getValue()); //convert from item  to metalist 메타리스트로 변환
            System.out.println(".................read 5");
            System.out.println("................." + metalist.getallvalue());
            int counter = 100;//edit later
            String[] ONEofValue;
            int keynum = tempMeta.getN();
            ONEofValue = metalist.getallvalue().split("&");
            for (int i = 0; i < counter; i++) {
                if (keynum-1 == i) {
                    System.out.println("suc   " + i  + "    " + ONEofValue[i]);
                    String temp = ONEofValue[i];
                    return temp;
                }
            }
        }
        else {
            oneMetaItem = malleDB.readKV(tempMeta.getid());
            System.out.println(oneMetaItem.getValue());
        }
        //export keyList in MalleDB/
        //export "one" metavalue in metalist


       /*
        while(true) {
            counter++;
            end = keyList.indexOf("^",start); //1st keyid
            ONEofKey = keyList.substring(start, end);
            if (ONEofKey.equals(metaId)) {
                start = 0;
                end = 0;
                String Metaallvalue = metalist.getallvalue();
                for (int i = 0; i < counter; i++) {
                    end = Metaallvalue.indexOf("^",start);
                    start = end + 1;
                }
                end = Metaallvalue.indexOf("^",start);

                ONEofValue = Metaallvalue.substring(start, end);
                return ONEofValue;
                //탈출
            }
            start = end + 1;

        }*/


        return null;

        //

    }

    public void serchpath(String directory_path, List<String> filepath) {//파일패스는 리스트사용
        File dir = new File(directory_path);
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                // Check if the file is a directory

                if (file.isDirectory()) {
                    // We will not print the directory name, just use it as a new
                    // starting point to list files from
                    serchpath(file.getAbsolutePath(), filepath);
                    System.out.println("............serchpath");
                    //recursive
                } else {
                    filepath.add(file.getAbsolutePath());
                }
            }
        }
    }

    //현재 버퍼단위로 메타리스트에 데이터를 읽고 쓰고 하지만 데이터들은 메타리스트 하나에 저장되는중 버퍼별로
    //메타리스트를 만들고 싶다면 추가수정. 특히 메타리스트의 키 부분 설정에 대한 포맷을 찾아야할것.
    public Status smallFileInsertEncoder(String driectory_path) {//parameter metaid, bufsize is will be change of delete
        String keyList = new String("");
        String valueList = new String("");
        List<String> filepath = new ArrayList();

        //init metalist
        Metalist metalist = new Metalist();
        metalist.setkey(malleDB.generateRandomString(6));          // change later to name or another
        String metaListId = metalist.getkey();       // change later to name or another

        //init files
        File dir = new File(driectory_path);
        File[] files = dir.listFiles();
        FileChannel inputChannel;


        try {
            //init buffer
            ByteBuffer buffer = ByteBuffer.allocateDirect(Options.BUFFER_SIZE);

            //seach file path
            serchpath(driectory_path, filepath);
            int row = 1;
            int col = 1;

            if (files != null && files.length > 0) {
                for (int i = 0; i < filepath.size(); i++) {
                    File file = new File(filepath.get(i));

                    inputChannel = new FileInputStream(file).getChannel();

                    //when buffer is full
                    if ((buffer.remaining() < file.length() + 2)) {
                        int pos = buffer.position();
                        buffer.flip();
                        //value[i] = Base64.getEncoder().encodeToString(buffer.array());
                        //Base64 can not Compatible with directbuffer. doesn't read data
                        // String V = Base64.getEncoder().encodeToString(b);
                        System.out.println(buffer.toString());
                        byte[] tempByte = new byte[pos];
                        buffer.get(tempByte);

                        for (int j = 0; j < pos - 1; j++) {
                            //tempByte[i] = buffer.get(i);
                            System.out.println("............" + "pos : " + pos);
                            System.out.println("............" + tempByte[j]);
                        }

                        String V = new String(tempByte);
                        System.out.println("............" + V);
                        metalist.addlist(V);
                        buffer.clear();
                        System.out.println(buffer.toString());

                        metalist.makemerge();
                        malleDB.insert(metaListId+(row++), metalist.getallvalue());//key : metalist's            value : metafiles's data
                        metalist = new Metalist();
                        metalist.setkey(malleDB.generateRandomString(6)); // change later to name or another
                        //check if existing key 코드작성
                        metaListId = metalist.getkey();       // change later to name or another
                         col=1;
                    }


                    //when buffer is not full
                    //made metafile for one small file
                    MetaFile meta = new MetaFile();
                    meta.setid("Meta_" + file.getName());//파일의 ID
                    meta.setname(file.getName());
                    meta.setsize((int) file.length());
                    meta.setMetaListId(metaListId+row);
                    meta.setN(col++);
                    malleDB.insert(meta.getid(), meta.toString()); //each metafile's value is metalistid
                    //key : file ID       value : meta info
                    System.out.println("............" + meta.getid());

                    inputChannel.read(buffer); //read data from inputChannel. and write data in buffer
                    buffer.put((byte) 38);      //add delimeter "&" each file
                    System.out.println(buffer.toString());

                        /*
                        int pos = buffer.position();
                        buffer.flip();
                        //value[i] = Base64.getEncoder().encodeToString(buffer.array());

                       // String V = Base64.getEncoder().encodeToString(b);
                        System.out.println(buffer.toString());
                        byte[] tempByte = new byte[pos];
                        buffer.get(tempByte);

                        for (int i = 0; i<pos-1; i++) {
                            //tempByte[i] = buffer.get(i);
                            System.out.println("............" +"pos : "  +pos);
                            System.out.println("............" +tempByte[i]);
                        }

                        String V = new String(tempByte);
                        System.out.println("............" + V);
                        metalist.addlist(V);

                        buffer.clear();
                        System.out.println(buffer.toString());
                        //if not directory,if it is file, read byte value from path using encoder
                        //and insert meta

                         */
                    //}
                }

                //when allfile is read . flush data
                if (buffer.position() > 0) {
                    int pos1 = buffer.position();
                    buffer.flip();
                    //value[i] = Base64.getEncoder().encodeToString(buffer.array());

                    // String V = Base64.getEncoder().encodeToString(b);
                    System.out.println(buffer.toString());
                    byte[] tempByte1 = new byte[pos1];
                    buffer.get(tempByte1);

                    /* // check byte data
                    for (int i = 0; i<pos1-1; i++) {
                        //tempByte[i] = buffer.get(i);
                        System.out.println("............" +"pos : "  +pos1);
                        System.out.println("............" +tempByte1[i]);
                    }*/

                    String V = new String(tempByte1);
                    System.out.println("............" + V);
                    metalist.addlist(V);                   //stored data in metalist
                    buffer.clear();
                    System.out.println(buffer.toString());

                    metalist.makemerge();
                    malleDB.insert(metaListId+row, metalist.getallvalue());//key : metalist's            value : metafiles's data
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }

        // insert keyList and metailst in MalleDB


        return Status.OK;
    }
/*
    public void smallFileDataRead(String startDir) {
        File dir = new File(startDir);
        File[] files = dir.listFiles();

        if (files != null && files.length > 0) {
            for (File file : files) {
                // Check if the file is a directory
                if (file.isDirectory()) {
                    // We will not print the directory name, just use it as a new
                    // starting point to list files from
                    smallFileDataRead(file.getAbsolutePath());
                } else {
                    // We can use .length() to get the file size
                    System.out.println(file.getAbsolutePath() + "  " + file.getName() + " (size in bytes: " + file.length() + ")");

                }
            }
        }
    }
*/
    public void smallFileDataRead(String startDir) {
        File dir = new File(startDir);
        File[] files = dir.listFiles();

        if (files != null && files.length > 0) {
            for (File file : files) {
                // Check if the file is a directory
                if (file.isDirectory()) {
                    // We will not print the directory name, just use it as a new
                    // starting point to list files from
                    smallFileDataRead(file.getAbsolutePath());
                } else {
                    // We can use .length() to get the file size
                    System.out.println(file.getAbsolutePath() + "  " + file.getName() + " (size in bytes: " + file.length() + ")");

                }
            }
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
