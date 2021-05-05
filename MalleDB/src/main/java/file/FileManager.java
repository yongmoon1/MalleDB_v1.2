package file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import db.MalleDB;
import util.*;
//test pull
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileManager {
    private final MalleDB malleDB;
    private final SmallFileManager smallFileManager;
    private final MiddleFileManager middleFileManager;
    private final BigFileManager bigFileManager;
    private String prefix = "123456";

    public int isBig(String filePath) {
        int size;
        File file = new File(filePath);
        if (file.isFile()) {
            size = Long.valueOf(file.length()).intValue();
            if (size > Options.BUFFER_SIZE + 1024*1024) return 2;
            else if(size < Options.BUFFER_SIZE) return 1;
            else{
                return 0;
            }

        } else {
            System.out.println("WRONG FILE PATH");
            return -1;
        }
    }

    public FileManager(MalleDB malleDB) {
        this.malleDB = malleDB;
        smallFileManager = new SmallFileManager(malleDB);
        middleFileManager = new MiddleFileManager(malleDB);
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
        String value = malleDB.read(key).getValue();
        MetaFile metaFile = new MetaFile();
        metaFile.Stringto(value);
        return metaFile;
    }

    public Status insertFile(String filename) throws IOException {
        System.out.println("Inserting File : " + filename);
          //일부 파일은 파일 path를 인자로 받는중
        File file = new File(filename);

        if (isBig(filename) == 2 ) {
            bigFileManager.bigFileInsert(filename);
        } else if(isBig(filename) == 1){
            middleFileManager.middleFileInsert(filename);
        }
        else {
            smallFileManager.smallFileInsertEncoder(filename);
        }
        return Status.OK;
    }
    public Status insertFile2(String filename,LinkedList<MetaFile> listOfMetaFiles) throws IOException {
        System.out.println("Inserting File : " + filename);
        //일부 파일은 파일 path를 인자로 받는중
        File file = new File(filename);

        if (isBig(filename) == 2 ) {
            bigFileManager.bigFileInsert(filename);
        } else if(isBig(filename) == 1){
            middleFileManager.middleFileInsert(filename);
        }
        else {
            smallFileManager.smallFileInsertEncoder(filename);//
        }//insert 방식을 어떻게 filepath에서 filename으로 바꿀것인가


         // this part is fatal error part. now smallFileInsertEncoder() is assume that insert many file from parameter of filepath

        //but now parameter is filename. so this func is only work about one file.
        // when file is smallfile, occur buffer wasted.
        // for solve this problem, we also add parameter of buffer



        //if we receive parameter to filename
        // we can serch filepath using serchpath()
        // but this way is must assume that all file's name diffrent each other
        //-the solution is serch filepath and remember, and check it often ?

       /* int listSize = Long.valueOf(file.length()).intValue()/Options.BUFFER_SIZE + 1;
        int listCsr=0;
        LinkedList<MetaFile> listOfMetaFiles= new LinkedList<MetaFile>();
        while(true){

            if( (( (listCsr+1)%listSize == 0) && ( listCsr!=0 )) || listCsr+1 == listSize ) {//this is isListFull() or all MetaFile read
                MetaFile tempMeta;
                int cycle = (listCsr)%listSize;//if size = 10 , cycle = 9
                for(int i = 0; i< cycle;  i++ ){
                    tempMeta = listOfMetaFiles.get(i);
                    malleDB.insert(tempMeta.getid(),tempMeta.toString());
                }
                if(listCsr+1 == listSize)break;
            }

            //MetaFile의 info 정의
            MetaFile meta = new MetaFile();
            meta.setid("Meta_" + file.getName());
            meta.setname(file.getName());
            meta.setsize((int) file.length());
            listOfMetaFiles.add((listCsr)%listSize,meta);
            listCsr++;
        }
         */
        //주석은 파라미터 파일패스일때 다수 파일에 대한 메타파일 제작 구문이다.//오류코드 참고만하다 삭제
        int listSize = 10;
        int listCsr=0;


            if( (( (listCsr+1)%listSize == 0) && ( listCsr!=0 )) || listCsr+1 == listSize ) {//this is isListFull() or all MetaFile read
                MetaFile tempMeta;
                int cycle = (listCsr)%listSize;//if size = 10 , cycle = 9
                for(int i = 0; i< cycle;  i++ ){
                    tempMeta = listOfMetaFiles.get(i);
                    malleDB.insert(tempMeta.getid(),tempMeta.toString());
                }
            }

            //MetaFile의 info 정의
            MetaFile meta = new MetaFile();
            meta.setid("Meta_" + file.getName());
            meta.setname(file.getName());
            meta.setsize((int) file.length());
            listOfMetaFiles.add((listCsr)%listSize,meta);
            listCsr++;
        //일단은 프로토로 개수로 사이즈가 정해짐. 추후 크기를 이용하여 리스트의 사이즈를 변경할수도있음



        return Status.OK;
    }
    public Status readFile(String metaID) throws IOException {
        String metaFileString = malleDB.read(metaID).getValue();
        MetaFile metaFile = new MetaFile();
        metaFile.Stringto(metaFileString);

        if(metaFile.isBig()==2){
            bigFileManager.bigFileRead(metaFile);
        }
        else if(metaFile.isBig()==1){
          //  middleFileManager.middleFileRead(metaFile);

        }
        else{
           //smallFileManager.smallFileDataRead(metaFile.getid());
           smallFileManager.smallOneFileRead(metaID);
        }
        return Status.OK;
    }

    public Status updateFile(String metaID) {
        String metaFileString = malleDB.read(metaID).getValue();
        MetaFile metaFile = new MetaFile();
        metaFile.Stringto(metaFileString);

        if (metaFile.isBig()==2) {
            bigFileManager.bigFileUpdate(metaFile);
        } else if(metaFile.isBig()==1){
            //middleFileManager.middleFileUpdate(metaFile);
        }
         else {
            // Small File Update
        }
        return Status.OK;
    }

    public Status deleteFile(String metaID) {
        String metaFileString = malleDB.read(metaID).getValue();
        MetaFile metaFile = new MetaFile();
        metaFile.Stringto(metaFileString);

        if (metaFile.isBig()==2) {
            bigFileManager.bigFileDelete(metaFile);
        } else if(metaFile.isBig()==1){
            middleFileManager.middleFileDelete(metaFile);
        }else {
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
