package util;

import java.util.Map;
import redis.clients.jedis.Pipeline;


public class HashMap {

    private static Integer map_size = 2;
    private static Map<String, String> map = new java.util.HashMap<>(map_size);

    public static Status insert(String key, String value){
        map.put(key, value);
        System.out.println("Inserting into Hashmap...");
        System.out.println(map.size());
        if(map.size()==map_size) return Status.HASHMAP_FULL;
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

    public static void flush_leveldb(Pipeline pipeline){
        for(Map.Entry<String, String> entry : map.entrySet()){
            pipeline.set(entry.getKey().getBytes(), entry.getValue().getBytes());
        }
        pipeline.sync();
        map.clear();
    }
}
