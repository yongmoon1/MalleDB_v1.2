package connectors;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import interfaces.SubDB;
import util.Item;
import util.Status;

import java.util.ArrayList;
import java.util.List;

import static redis.clients.jedis.HostAndPort.localhost;


public class Redis extends SubDB{

    private static JedisPoolConfig jedisPoolConfig = null;
    private static JedisPool pool = null;


    @Override
    public Status init(){

        jedisPoolConfig = new JedisPoolConfig();
        pool = new JedisPool(jedisPoolConfig, "127.0.0.1");
        Jedis jedis = pool.getResource();
        return Status.OK;
    }

    @Override
    public Status close(){


    }

    @Override
    public Status insert(Item item){


    }

    @Override
    public Item readMeta(Item item) {

    }

    @Override
    public List<Item> readAll(String table, Item item) {


    }

    @Override
    public Status update(String table, Item item) {


    }

    @Override
    public Status delete(String table, Item item) {

    }
}

