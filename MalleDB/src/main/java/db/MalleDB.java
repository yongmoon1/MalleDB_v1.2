package db;
import connectors.*;
import interfaces.SubDB;
import util.*;
import java.util.*;

import static util.Options.SUB_DB;

public class MalleDB implements interfaces.MalleDB {

    private SubDB metadb;
    private SubDB blockdb;
    private SubDB mdb;
    private SubDB bdb;
    private SubDB tdb;
    private boolean usingOneSubDB = false;

    public byte[] smallFilesbuffer;

    //Initialize with default configuration
    @Override
    public Status init() {
        return init(new Options());
    }

    //Initialize with custom configuration
    @Override
    public Status init(Options options) {
        // BRANCH TEST
        if (SUB_DB == Options.DB_TYPE.MYSQL) {
            metadb = new MySQL();
        } else if (SUB_DB == Options.DB_TYPE.LEVELDB || SUB_DB== Options.DB_TYPE.TDLEVELDB) {
            metadb = new LevelDB();
        } else if (SUB_DB == Options.DB_TYPE.CASSANDRA || SUB_DB == Options.DB_TYPE.TDCASSANDRA) {
            metadb = new Cassandra();
        } else if (SUB_DB == Options.DB_TYPE.REDIS || SUB_DB== Options.DB_TYPE.TDREDIS) {
            metadb = new Redis();
        } else if (SUB_DB == Options.DB_TYPE.POSTGRESQL) {
            metadb = new PostgreSQL();
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
            } else if (SUB_DB == Options.DB_TYPE.POSTGRESQL) {
                blockdb = new PostgreSQL();
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

        if (SUB_DB == Options.DB_TYPE.TDLEVELDB || SUB_DB == Options.DB_TYPE.TDCASSANDRA || SUB_DB == Options.DB_TYPE.TDREDIS) {
            System.out.println("Direct Inserting Key: " + key);
            blockdb.direct_insert(key, value);
        }

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
            if (SUB_DB == Options.DB_TYPE.MYSQL || SUB_DB == Options.DB_TYPE.POSTGRESQL) {
                // For RDBMS
                for (int i = 0; i < Options.bCOUNTER; i++) {
                    if (item.getCounters()[i] > 0) {
                        blockdb.delete(Options.TABLES_MYSQL[i], item);
                        System.out.println("Block deleted...");
                    }
                }
            } else {  // For NO-SQL
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


    public Status direct_create() {
        blockdb.direct_create();
        return Status.OK;
    }

    public void direct_insert(String key, String value) {
        System.out.println("Direct Inserting Key: " + key);
        blockdb.direct_insert(key, value);
    }

    public void direct_read(String key) {
        System.out.println("Direct Reading Key: " + key);
        blockdb.direct_read(key);
    }

    public void direct_update(String key, String value) {
        System.out.println("Direct Updating Key: " + key);
        blockdb.direct_update(key, value);
    }

    public void direct_delete(String key) {
        System.out.println("Direct Deleting Key: " + key);
        blockdb.direct_delete(key);
    }

    public String[][] select(String query) {
        return blockdb.select(query);

//        for(int i = 0; i<db.getROW();i++) {
//            for (int j = 0; j < db.getCOL(); j++)
//            { System.out.println(Result[i][j]);}
//
//        }
//        return Status.OK;//??? ?????? ?????? ??????
    }

    public Status execute(String query) {
        return blockdb.execute(query);
    }

    public Status flush_query(String[] query) {
        return blockdb.flush_query(query);
    }

    public static String generateRandomString(int n) {

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

    public Item readKV(String key) {
        //Initialize the Item
        Item item = new Item();
        item.setKey(key);
        item = metadb.readMeta(item);
        StringBuilder sb = new StringBuilder();

        //Read each block from the sub database
        if(usingOneSubDB) {
            for (int i = 0; i < Options.bCOUNTER; i++) {
                List<Item> blocks = blockdb.readAll(Options.TABLES_MYSQL[i], item);
                for (Item block : blocks) {
                    //Append with the value
                    sb.append(block.getValue());
                }
            }
        }else{
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
        item.setValue(sb.toString());
        return item;
    }
}
