package example.jedis;

import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JedisCluster {
    public static void main(String[] args) {
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWait(Duration.ofSeconds(1));
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(1));

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        jedisClusterNodes.add(new HostAndPort("localhost", 6379));
        jedisClusterNodes.add(new HostAndPort("localhost", 6380));
        jedisClusterNodes.add(new HostAndPort("localhost", 6381));
        jedisClusterNodes.add(new HostAndPort("localhost", 6382));
        jedisClusterNodes.add(new HostAndPort("localhost", 6383));
        jedisClusterNodes.add(new HostAndPort("localhost", 6384));
        DefaultJedisClientConfig config = DefaultJedisClientConfig.builder()/*.password("bitnami")*/.build();

        redis.clients.jedis.JedisCluster jedisCluster = new redis.clients.jedis.JedisCluster(jedisClusterNodes, config, 10, poolConfig);
        jedisCluster.set("foo", "bar");
        System.out.println(jedisCluster.get("foo"));

        Map<String, String> hash = new HashMap<>();
        hash.put("name", "John");
        hash.put("surname", "Smith");
        hash.put("company", "Redis");
        hash.put("age", "29");
        jedisCluster.hset("user-session:123", hash);
        System.out.println(jedisCluster.hgetAll("user-session:123"));
        jedisCluster.zadd("sose", 0, "car");
        jedisCluster.zadd("sose", 0, "bike");
        System.out.println(jedisCluster.zrange("sose", 0, -1));
        jedisCluster.close();
    }
}
