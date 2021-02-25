package db;

import connectors.*;
import interfaces.SubDB;
import util.*;
import util.HashMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static util.Options.SUB_DB;

public class MalleDB implements interfaces.MalleDB {

    private SubDB metadb;
    private SubDB blockdb;
    private SubDB mdb;
    private SubDB bdb;
    private SubDB tdb;
    private Options.DB_TYPE onlyOneType = Options.DB_TYPE.LEVELDB;
    private boolean usingOneSubDB = false;
    private String prefix = "123456";

    /*
        public static void main(String[] args) {

           // String key = generateRandomString(20);
           // System.out.println("Before: " + key);

            //byte[] arr = key.getBytes();
            //String key1 = new String(arr);

            //System.out.println("After: " + key1);


            MalleDB malleDB = new MalleDB();
            Options options = new Options(Options.DB_TYPE.MYSQL, Options.DB_TYPE.CASSANDRA, Options.DB_TYPE.LEVELDB);
            malleDB.init(options);
            malleDB.create();

            //4294304

            String key = generateRandomString(20);
            String value = generateRandomString(500);

            System.out.println("Key: " + key);
            System.out.println("Value: " + value);

            malleDB.insert(key, value);


            malleDB.read(key);

           // String key = "E1O1IByghZN0ryG7raPf";
            malleDB.delete(key);


            malleDB.close();

        }
    */
    //Initialize with default configuration
    @Override
    public Status init() {
        return init(new Options());
    }

    //Initialize with custom configuration
    @Override
    public Status init(Options options) {

        if (SUB_DB == Options.DB_TYPE.MYSQL) {
            metadb = new MySQL();
            onlyOneType = Options.DB_TYPE.MYSQL;
        } else if (SUB_DB == Options.DB_TYPE.LEVELDB) {
            metadb = new LevelDB();
            onlyOneType = Options.DB_TYPE.LEVELDB;
        } else if (SUB_DB == Options.DB_TYPE.CASSANDRA) {
            metadb = new Cassandra();
            onlyOneType = Options.DB_TYPE.CASSANDRA;
        } else if (SUB_DB == Options.DB_TYPE.REDIS) {
            metadb = new Redis();
            onlyOneType = Options.DB_TYPE.REDIS;
        }

        metadb.init();


        if (options.isUsingDefault()) {
            usingOneSubDB = true;
            if (SUB_DB == Options.DB_TYPE.MYSQL) {
                blockdb = new MySQL();
            } else if (SUB_DB == Options.DB_TYPE.LEVELDB) {
                blockdb = new LevelDB();
            } else if (SUB_DB == Options.DB_TYPE.CASSANDRA) {
                blockdb = new Cassandra();
            } else if (SUB_DB == Options.DB_TYPE.REDIS) {
                blockdb = new Redis();
            }

            blockdb.init();

            mdb = null;
            bdb = null;
            tdb = null;

        } else {

            if (options.getDbMedium() == Options.DB_TYPE.MYSQL) {
                mdb = new MySQL();
            } else if (options.getDbMedium() == Options.DB_TYPE.LEVELDB) {
                mdb = new LevelDB();
            } else {
                mdb = new Cassandra();
            }

            if (options.getDbBlob() == Options.DB_TYPE.MYSQL) {
                bdb = new MySQL();
            } else if (options.getDbBlob() == Options.DB_TYPE.LEVELDB) {
                bdb = new LevelDB();
            } else {
                bdb = new Cassandra();
            }

            if (options.getDbTiny() == Options.DB_TYPE.MYSQL) {
                tdb = new MySQL();
            } else if (options.getDbTiny() == Options.DB_TYPE.LEVELDB) {
                tdb = new LevelDB();
            } else {
                tdb = new Cassandra();
            }

            metadb.init();
            mdb.init();
            bdb.init();
            tdb.init();
            blockdb = null;
        }
        return Status.OK;
    }

    //Create databses and tables
    @Override
    public Status create() {
        metadb.create();
        if (usingOneSubDB) {
            blockdb.create();
        } else {
            mdb.create();
            bdb.create();
            tdb.create();
        }
        return Status.OK;
    }

    //Close the connection
    @Override
    public Status close() {
        metadb.close();
        if (usingOneSubDB) {
            blockdb.close();
        } else {
            mdb.close();
            bdb.close();
            tdb.close();
        }
        return null;
    }


    //Insert data
    @Override
    public Status insert(String key, String value) {

        //Initialize the variables starts
        String chunk;
        int order;
        Item meta;
        ArrayList<Item>[] blocks = new ArrayList[Options.bCOUNTER];
        for (int i = 0; i < Options.bCOUNTER; i++) {
            blocks[i] = new ArrayList<>();
        }
        int[] counters = new int[Options.bCOUNTER];
        //Initialize the variables ends

        //Memtable managements part starts
        for (int i = 0; i < Options.bCOUNTER; i++) {
            //check if the value size smaller than the block but the block is not tiny
            if (value.length() < Options.BLOCKS[i] && i != Options.bCOUNTER - 1) {
                continue;
            }
            //Initialize the order to 0
            order = 0;
            //Repeat while the value size is not 0
            while (!value.equals("")) {
                order++;
                //Check if the block is tiny and value size is smaller
                if (i == Options.bCOUNTER - 1 && value.length() <= Options.BLOCKS[i]) {
                    chunk = value;
                    value = "";
                } else { //Otherwise
                    //Break the while if the value size is smaller
                    if (value.length() < Options.BLOCKS[i]) {
                        order--;
                        break;
                    }
                    //Initialize the chunk as the block size
                    chunk = value.substring(0, Options.BLOCKS[i]);
                    //Update the value
                    value = value.substring(Options.BLOCKS[i]);
                }
                //Add new block to Memtable
                blocks[i].add(new Item(order, i + 1, key, chunk));
            }
            //Update metadata counter
            counters[i] = order;
        }
        //Memtable managements part ends

        //Database part starts
        //Initialize and insert metadata
        meta = new Item(key, counters);
        metadb.insert(meta);

        //Insert to databases
        if (usingOneSubDB) {
            for (int i = 0; i < Options.bCOUNTER; i++) {
                for (int j = 0; j < blocks[i].size(); j++) {
                    blockdb.insert(blocks[i].get(j));
                }
            }
        } else {
            for (int i = 0; i < blocks[0].size(); i++) {
                mdb.insert(blocks[0].get(i));
            }
            for (int i = 0; i < blocks[1].size(); i++) {
                bdb.insert(blocks[1].get(i));
            }
            for (int i = 0; i < blocks[2].size(); i++) {
                tdb.insert(blocks[2].get(i));
            }
        }
        //Database part ends

        blockdb.flush();    // To prevent Data is remained.

        return Status.OK;
    }

    public Status direct_create(){
        blockdb.direct_create();
        return Status.OK;
    }

    public void direct_insert(String key, String value){
        System.out.println("Direct Inserting Key: " + key);
        blockdb.direct_insert(key, value);
    }

    public void direct_read(String key){
        System.out.println("Direct Reading Key: " + key);
        blockdb.direct_read(key);
    }

    public void direct_update(String key, String value){
        System.out.println("Direct Updating Key: " + key);
        blockdb.direct_update(key, value);
    }

    public void direct_delete(String key){
        System.out.println("Direct Deleting Key: " + key);
        blockdb.direct_delete(key);
    }

    /*
    @Override
    public Status insert(List<String> keys, List<String> values) {

        String chunk;
        int order;
        List<Item> metaList = new ArrayList<>();
        ArrayList<Item>[] blocks = new ArrayList[Options.bCOUNTER];
        int[] counters = new int[Options.bCOUNTER];

        for(int n = 0; n < values.size(); n++) {
            //TODO: implement
        }

        for(int i = 0; i < metaList.size(); i++){
            metadb.insert(metaList.get(i));
        }
        for(int i = 0; i < Options.bCOUNTER; i++){
            for (int j = 0; j < blocks[i].size(); j++)
                blockdb.insert(blocks[i].get(j));
        }
        return Status.OK;
    }
*/
    public Status insertMetaFile(MetaFile newmeta) {
        String key;
        String metaInfo;
        metaInfo = newmeta.toString();
        key = prefix + newmeta.getKey();//change random generate key
        return insert(key, metaInfo);
    }

    public Status updateMetaFile(String key, MetaFile newmeta) {
        //뉴 메타 파일의 정보에서 기존 메타파일과 동일점 찾아서 업데이트
        deleteMetaFile(key);
        String metaInfo = newmeta.toString();
        return insert(key, metaInfo);
    }

    public Status deleteMetaFile(String key) {
        return delete(key);
    }

    public MetaFile readMetaFile(String key) {
        String value = read(key).getValue();
        MetaFile metaFile = new MetaFile();
        metaFile.Stringto(value);
        return metaFile;
    }

    public Status insertFile(String filename) {
        System.out.println("Inserting File : " + filename);
        String value = encoder(filename);
        return insert(filename, value);
    }

    public Status readFile(String filename) {
        System.out.println("Reading Fiile : " + filename);
        Status status = read(filename);
        if (status.isOk()) {
            String value = status.getValue();
            decoder(value, filename);
            return Status.OK;
        }
        return Status.ERROR;
    }

    public void updateFile(String filename){
        deleteFile(filename);
        insertFile(filename);
    }

    public void deleteFile(String filename){
        delete(filename);
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

    public String[][] select(String query){
        //if(!(blockdb || mdb || tdb || bdb));
        MySQL db = new MySQL();
        db.init(); //change to use existing DB

        String[][] Result = db.select(Options.DB_MYSQL, query);
        return Result;
//        for(int i = 0; i<db.getROW();i++) {
//            for (int j = 0; j < db.getCOL(); j++)
//            { System.out.println(Result[i][j]);}
//
//        }
//        return Status.OK;//열 개수 파악 추가
    }

    @Override
    public Status execute(String query){
        MySQL db = new MySQL();
        db.init();
        return db.execute(Options.DB_MYSQL, query);
    }

    @Override
    public Status flush(String[] query){
        MySQL db = new MySQL();  //
        db.init();
        return db.flush(Options.DB_MYSQL, query);
    }

    public String[][] select_post(String query){
        PostgreSQL db = new PostgreSQL();
        db.init();
        db.create();
        return db.select(Options.DB_POST, query);
    }

    public Status execute_post(String query){
        PostgreSQL db = new PostgreSQL();
        db.init();
        return db.execute(Options.DB_MYSQL, query);
    }

    @Override
    public Status read(String key) {
        //Initialize the Item
        Item item = new Item();
        item.setKey(key);
        item = metadb.readMeta(item);
        StringBuilder sb = new StringBuilder();

        //Read each block from the sub database
        if (usingOneSubDB) {
            for (int i = 0; i < Options.bCOUNTER; i++) {
                List<Item> blocks = blockdb.readAll(Options.TABLES_MYSQL[i], item);
                for (Item block : blocks) {
                    //Append with the value
                    sb.append(block.getValue());
                }
            }
        } else {
            List<Item> mblocks = mdb.readAll(Options.TABLES_MYSQL[0], item);
            for (Item block : mblocks) {
                sb.append(block.getValue());
            }

            List<Item> bblocks = bdb.readAll(Options.TABLES_MYSQL[1], item);
            for (Item block : bblocks) {
                sb.append(block.getValue());
            }

            List<Item> tblocks = tdb.readAll(Options.TABLES_MYSQL[2], item);
            for (Item block : tblocks) {
                sb.append(block.getValue());
            }
        }
        System.out.println("Read Value: " + sb.toString());
        return new Status("READ_OK", "Success", key, sb.toString());
    }

    @Override
    public Status update(String key, String value) {
        delete(key);
        return insert(key, value);
    }

    @Override
    public Status delete(String key) {
        Item item = new Item();
        item.setKey(key);
        item = metadb.readMeta(item);

        if (usingOneSubDB) {
            if(onlyOneType== Options.DB_TYPE.MYSQL) {
                for (int i = 0; i < Options.bCOUNTER; i++) {
                    if (item.getCounters()[i] > 0) {
                        blockdb.delete(Options.TABLES_MYSQL[i], item);
                        System.out.println("Block deleted...");
                    }
                }
            }else{
                blockdb.deleteAll(item);
            }
        } else {
            if (item.getCounters()[0] > 0) {
                mdb.delete(Options.TABLES_MYSQL[0], item);
            }
            if (item.getCounters()[1] > 0) {
                bdb.delete(Options.TABLES_MYSQL[1], item);
            }
            if (item.getCounters()[2] > 0) {
                tdb.delete(Options.TABLES_MYSQL[2], item);
            }
        }
        metadb.delete(Options.TABLE_META_MYSQL, item);
        System.out.println("Meta deleted...");
        return Status.OK;
    }

    static String generateRandomString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString Optionsiable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
