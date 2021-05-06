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
        int sourceSize = Long.valueOf(file.length()).intValue();

        if (isBig(filename) == 2 ) {
            bigFileManager.bigFileInsert(filename);
        } else if(isBig(filename) == 1){
            middleFileManager.middleFileInsert(filename);
        } else {
            if(malleDB.smallFilesbuffer == null){malleDB.smallFilesbuffer = new byte[sourceSize];}
            smallFileManager.smallOneFileInsert(filename, malleDB.smallFilesbuffer,sourceSize);//
        }//insert 방식을 어떻게 filepath에서 filename으로 바꿀것인가



        // 모든 파일이 경로가 다르면, 현재의 스몰파일의 경우 버퍼의 지속성이 어려워진다.
        // 그렇기에 파일네임으로만 스몰파일인서트를 실행하고 싶으면 외부에서 버퍼를 받아와야한다.
        //버퍼의 지속성 문제
        //버퍼는 내부에서 정의 된후 유지되어야한다. 가비지 처리 되나 안되나?
        //리턴주소를 버퍼로 하면 어떨까
        //malleDB내에 버퍼를 정의하면 될수도있다.


        //혹은 스몰파일 인서트시 버퍼정의시 이전 파일의 메타파일에 저장된 버퍼주소를 불러오는 형식은 어떨까
        //이것은 물론 가비지 컬렉션을 피해야함

        meta.setid("Meta_" + file.getName());
        meta.setname(file.getName());
        meta.setsize((int) file.length());
        listOfMetaFiles.add((listCsr)%listSize,meta);
        listCsr++;




       /* int listize = Long.valueOf(file.length()).intValue()/Options.BUFFER_SIZE + 1;
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

        //일단은 프로토로. 개수로 사이즈가 정해짐. 추후 크기를 이용하여 리스트의 사이즈를 변경할수도있음



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
