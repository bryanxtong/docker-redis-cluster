package example.lettuce;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Arrays;

public class SyncCommonPool {

    public static void main(String[] args) {
        jedisClusterNodes.add(new HostAndPort("localhost", 6379));
        jedisClusterNodes.add(new HostAndPort("localhost", 6380));
        jedisClusterNodes.add(new HostAndPort("localhost", 6381));
        jedisClusterNodes.add(new HostAndPort("localhost", 6382));
        jedisClusterNodes.add(new HostAndPort("localhost", 6383));
        jedisClusterNodes.add(new HostAndPort("localhost", 6384));
        RedisClusterClient redisClient = RedisClusterClient.create(Arrays.asList(uri1, uri2, uri3, uri4, uri5, uri6));
        //redisClient.getPartitions();
        GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool = ConnectionPoolSupport.createGenericObjectPool
                (() -> redisClient.connect(StringCodec.UTF8),
                        new GenericObjectPoolConfig<>());
        try (StatefulRedisClusterConnection<String, String> connection = pool.borrowObject()) {
            RedisAdvancedClusterCommands<String, String> commands = connection.sync();
            commands.set("key", "value");
            commands.blpop(10, "list");
            System.out.println(commands.get("key"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        pool.close();
        redisClient.shutdown();
    }
}
