package util;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import redis.clients.jedis.Pipeline;


public class HashMap {  // Utility Class

    private static Integer insert_size = 50;
    private static Map<String, String> map = new java.util.HashMap<>(insert_size);
    private static Queue<String> queue = new LinkedList<>();

    public static Status insert(String key, String value){
        map.put(key, value);
        System.out.println("Adding 'INSERT' into Hashmap...");
        System.out.println(map.size());
        if(map.size()== insert_size) return Status.HASHMAP_FULL;
        else return Status.OK;
    }

    public static void flush_redis(Pipeline pipeline){
        for(Map.Entry<String, String> entry : map.entrySet()){
            pipeline.set(entry.getKey().getBytes(), entry.getValue().getBytes());
        }
        pipeline.sync();
        System.out.println("Flushing to Redis Server...");
        map.clear();
    }

    public static void flush_leveldb(WriteBatch batch, DB db){
        for(Map.Entry<String, String> entry : map.entrySet()){
            batch.put(entry.getKey().getBytes(), entry.getValue().getBytes());
        }
        System.out.println("Flushing to leveldb");
        db.write(batch);
        map.clear();
    }
}
