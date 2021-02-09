package connectors;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import interfaces.SubDB;
import util.Item;
import util.Options;
import util.Status;

import java.util.ArrayList;
import java.util.List;

import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static redis.clients.jedis.HostAndPort.localhost;


public class Redis extends SubDB{

    private static JedisPoolConfig jedisPoolConfig = null;
    private static JedisPool pool = null;
    Jedis jedis = null;

    @Override
    public Status init(){
        jedisPoolConfig = new JedisPoolConfig();
        pool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379);
        jedis = pool.getResource();
        return Status.OK;
    }

    @Override
    public Status close(){
        if(jedis != null){
            jedis.close();
        }
        return Status.OK;
    }

    @Override
    public Status insert(Item item){
        String key = item.getType() + util.Options.DELIM + item.getOrder() + util.Options.DELIM + item.getKey();
        String value = item.getValue();
        System.out.println("Inserting: Key: " + key + " Value: " + value);
        jedis.set(key.getBytes(), value.getBytes());
        return Status.OK;
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
            byte[] value = jedis.get(key.getBytes());


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
        jedis.del(bytes(key));
        return Status.OK;
    }
}

