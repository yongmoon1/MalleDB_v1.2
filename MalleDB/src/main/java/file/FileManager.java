package file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import db.MalleDB;
import util.*;
//test pull
import java.io.*;

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
            if (size > Options.BUFFERSIZE) return true;
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
        String value = malleDB.read(key).getValue();
        MetaFile metaFile = new MetaFile();
        metaFile.Stringto(value);
        return metaFile;
    }

    public Status insertFile(String filename) throws IOException {
        System.out.println("Inserting File : " + filename);

        File file = new File(filename);

        if (isBig(filename)) {
            bigFileManager.bigFileInsert(filename);
            return Status.OK;
        } else {
            smallFileManager.smallFileInsertEncoder(filename);
            return Status.OK;
        }
    }

    public Status readFile(String filename) {
        System.out.println("Reading Fiile : " + filename);
        Status status = malleDB.read(filename);
        if (status.isOk()) {
            String value = status.getValue();
            decoder(value, filename);
            return Status.OK;
        }
        return Status.ERROR;
    }

    public void updateFile(String filename) throws IOException {
        deleteFile(filename);
        insertFile(filename);
    }

    public void deleteFile(String filename) {
        malleDB.delete(filename);
    }

    private String encoder(String imagePath) {
        String base64Image = "";
        byte imageData[] = {};
        File file = new File(imagePath);
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            // Reading a Image file from file system
            imageData = new byte[(int) file.length()];
            BufferedInputStream bis = new BufferedInputStream(imageInFile);
            int size = bis.read(imageData);
            //base64Image = Base64.getEncoder().encodeToString(imageData);
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
        // return base64Image;
        return new String(imageData);
    }

    private void decoder(String value, String pathFile) {
        try (FileOutputStream imageOutFile = new FileOutputStream(pathFile)) {
            // Converting a Base64 String into Image byte array
            //byte[] imageByteArray = Base64.getDecoder().decode(base64Image);
            System.out.println("Creating File");
            BufferedOutputStream bos = new BufferedOutputStream(imageOutFile);
            bos.write(value.getBytes());
            bos.flush();
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
    }
}
