package connectors;

import it.unimi.dsi.fastutil.Hash;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import interfaces.SubDB;
import util.Item;
import util.Options;
import util.Status;
import util.HashMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static redis.clients.jedis.HostAndPort.localhost;


public class Redis extends SubDB {

    private static JedisPoolConfig jedisPoolConfig = null;
    private static JedisPool pool = null;
    private static Jedis jedis = null;
    private static Pipeline pipeline = null;
    boolean assigned = false;
    private static Integer read_size = 50;

    @Override
    public Status init() {
        if (!assigned) {
            jedisPoolConfig = new JedisPoolConfig();
            pool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379);
            jedis = pool.getResource();
            pipeline = jedis.pipelined();
            assigned = true;
        }
        return Status.OK;
    }

    @Override
    public Status close() {
        if (jedis != null && assigned) {
            jedis.close();
        }
        assigned = false;
        return Status.OK;
    }

    @Override
    public Status insert(Item item) {
        Status ins_check;
        if (item.isMeta()) {
            String key = item.getKey();
            String value =
                    item.getCounters()[0] + util.Options.DELIM + item.getCounters()[1] + util.Options.DELIM + item.getCounters()[2];
            ins_check = HashMap.insert(key, value);
            System.out.println("Metadata for key \"" + item.getKey() + "\" inserted...");
        } else {
            String key = item.getType() + util.Options.DELIM + item.getOrder() + util.Options.DELIM + item.getKey();
            String value = item.getValue();
            ins_check = HashMap.insert(key, value);
            System.out.println("Inserting: Key: " + key + " Value: " + value);
            //jedis.set(key.getBytes(), value.getBytes());
        }

        if (ins_check == Status.HASHMAP_FULL) {
            System.out.println("Hash_IS_FULL");
            HashMap.flush_redis(pipeline);
        }

        return Status.OK;
    }

    @Override
    public void flush() {
        HashMap.flush_redis(pipeline);
    }

    @Override
    public Item readMeta(Item item) {
        String key = item.getKey();
        String value = new String(jedis.get(key.getBytes()));
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
        List<Response> responses = new ArrayList<>();
        Integer pipeSize = 0;
        int index = 0;
        for (int i = 0; i < util.Options.bCOUNTER; i++) {
            if (table.equals(util.Options.TABLES_MYSQL[i])) {
                index = i;
            }
        }

        for (int i = 1; i <= item.getCounters()[index]; i++) {
            String key = (index + 1) + util.Options.DELIM + i + util.Options.DELIM + item.getKey();
            responses.add(pipeline.get(key));
            pipeSize++;
            System.out.println("Pipelining Read: Key: " + key);
            //byte[] value = jedis.get(key.getBytes());

            if (pipeSize == read_size) {
                pipeline.sync();
                System.out.println("Flushing READ");
                for (Response response : responses) {
                    Object o = response.get();
                    items.add(new Item(i, item.getType(), item.getKey(), o.toString()));
                }
                responses.clear();
                pipeSize = 0;
            }
            //item.setValue(Arrays.toString(value));
        }
        // To flush remained READ instruction.
        pipeline.sync();
        System.out.println("Flushing READ at last");
        for (Response response : responses) {
            Object o = response.get();
            items.add(new Item(0, item.getType(), item.getKey(), o.toString()));
            // Don't care order
        }
        responses.clear();

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

        jedis.del(bytes(key));
        return Status.OK;
    }

    @Override
    public Status deleteAll(Item item) {
        for (int index = 0; index < Options.bCOUNTER; index++) {
            for (int i = 1; i <= item.getCounters()[index]; i++) {
                String key = (index + 1) + util.Options.DELIM + i + util.Options.DELIM + item.getKey();
                pipeline.del(key);
                System.out.println("Pipelining Delete: Key: " + key);
            }
        }
        System.out.println("Flushing DELETE");
        pipeline.sync();
        return Status.OK;
    }
    @Override
    public Status direct_insert(String key, String value){
        jedis.set(key.getBytes(), value.getBytes());
        System.out.println(key.getBytes());
        return Status.OK;
    }

    @Override
    public Status direct_read(String key){
        String data = new String(jedis.get(key.getBytes()));
        System.out.println("Value: " + data);
        return Status.OK;
    }

    @Override
    public Status direct_update(String key, String value){
        direct_delete(key);
        direct_insert(key, value);
        return Status.OK;
    }

    @Override
    public Status direct_delete(String key){
        jedis.del(key.getBytes());
        return Status.OK;
    }
}

