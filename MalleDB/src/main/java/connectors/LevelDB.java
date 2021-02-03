package connectors;

import interfaces.SubDB;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import static org.fusesource.leveldbjni.JniDBFactory.*;
import util.Item;
import util.Status;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelDB extends SubDB {

    private static Options options = null;
    private static DB db = null;
    private static boolean assigned = false;    // If LevelDB already exists, assigned=true.

    @Override
    public Status init() {
        if(!assigned){
            try {
                options = new Options();
                options.createIfMissing(true);
                db = factory.open(new File(util.Options.DB_LEVELDB), options);
                assigned = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            assigned = true;    // Flag that LevelDB exists.
            return Status.OK;
        }
        else{
            return null;    // If LevelDB already exists, Nothing's done.
        }
    }

    @Override
    public Status close() {
        try {
            if(db != null)
                db.close();
            options = null;
            db = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        assigned = false;   // Unset the flag
        return Status.OK;
    }


    @Override
    public Status insert(Item item) {
        if(item.isMeta()){
            String key = item.getKey();
            String value =
                    item.getCounters()[0] + util.Options.DELIM + item.getCounters()[1]+ util.Options.DELIM + item.getCounters()[2];
            db.put(key.getBytes(), value.getBytes());
            System.out.println("Metadata for key \"" + item.getKey() + "\" inserted...");
        }
        else{
            String key = item.getType() + util.Options.DELIM + item.getOrder() + util.Options.DELIM + item.getKey();
            String value = item.getValue();
            System.out.println("Inserting: Key: " + key + " Value: " + value);
            db.put(key.getBytes(), value.getBytes());
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
}
