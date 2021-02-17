package connectors;

import interfaces.SubDB;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import static org.fusesource.leveldbjni.JniDBFactory.*;

import org.iq80.leveldb.WriteBatch;
import util.HashMap;
import util.Item;
import util.Status;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelDB extends SubDB {

    private static Options options = null;
    private static DB db = null;
    private static boolean assigned = false;    // If LevelDB already exists, assigned=true.
    private static Integer read_size = 3;
    private static WriteBatch batch;

    @Override
    public Status init() {
        if(!assigned){
            try {
                options = new Options();
                options.createIfMissing(true);
                db = factory.open(new File(util.Options.DB_LEVELDB), options);
                assigned = true;    // Flag that LevelDB exists.
                batch = db.createWriteBatch();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Status.OK;
        }
        else{
            return null;    // If LevelDB already exists, Nothing's done.
        }
    }

    @Override
    public Status close() {
        try {
            if(db != null && assigned)
                db.close();
            options = null;
            db = null;
            assigned = false;   // Unset the flag
            batch.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Status.OK;
    }


    @Override
    public Status insert(Item item) {
        Status ins_check;
        if(item.isMeta()){
            String key = item.getKey();
            String value =
                    item.getCounters()[0] + util.Options.DELIM + item.getCounters()[1]+ util.Options.DELIM + item.getCounters()[2];
            ins_check = HashMap.insert(key, value);
            System.out.println("Metadata for key \"" + item.getKey() + "\" inserted...");
        }
        else{
            String key = item.getType() + util.Options.DELIM + item.getOrder() + util.Options.DELIM + item.getKey();
            String value = item.getValue();
            ins_check = HashMap.insert(key, value);
            System.out.println("Inserting: Key: " + key + " Value: " + value);
        }

        if (ins_check == Status.HASHMAP_FULL) {
            System.out.println("Hash_IS_FULL");
            HashMap.flush_leveldb(batch, db);
        }

        return Status.OK;
    }

    @Override
    public Item readMeta(Item item) {
        String key = item.getKey();
        String value = new String(db.get(key.getBytes()));
        int[] counters = new int[3];
        String[] splitArr = value.split(util.Options.DELIM);
        counters[0] = Integer.parseInt(splitArr[0]);    // m_count
        counters[1] = Integer.parseInt(splitArr[1]);    // b_count
        counters[2] = Integer.parseInt(splitArr[2]);    // t_count
        item.setCounters(counters);
        System.out.println("Item \"" + key + "\" is retrieved...");
        return item;
    }

    @Override
    public List<Item> readAll(String table, Item item) {
        List<Item> items = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < util.Options.bCOUNTER; i++){
            if(table.equals(util.Options.TABLES_MYSQL[i])){
                index = i;
            }
        }

        for(int i = 1; i <= item.getCounters()[index]; i++) {
            String key = (index + 1) + util.Options.DELIM + i + util.Options.DELIM + item.getKey();
            System.out.println("Reading: Key: " + key);
            byte[] value = db.get(key.getBytes());


            items.add(new Item(i, item.getType(), item.getKey(), new String(value)));
            //item.setValue(Arrays.toString(value));
        }
        return items;
    }

    @Override
    public Status update(String table, Item item) {
        Status status;
        status = delete(table, item);
        status = insert(item);
        return status;
    }

    @Override
    public Status delete(String table, Item item) {
        String key = item.getKey();
        db.delete(bytes(key));
        return Status.OK;

    }

    @Override
    public Status deleteAll(Item item) {
        for (int index = 0; index < util.Options.bCOUNTER; index++) {
            for (int i = 1; i <= item.getCounters()[index]; i++) {
                String key = (index + 1) + util.Options.DELIM + i + util.Options.DELIM + item.getKey();
                batch.delete(bytes(key));
                System.out.println("Pipelining Delete: Key: " + key);
            }
        }
        System.out.println("Flushing DELETE");
        db.write(batch);
        return Status.OK;
    }
}
