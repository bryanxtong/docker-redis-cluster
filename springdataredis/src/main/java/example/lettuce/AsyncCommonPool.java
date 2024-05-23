package example.lettuce;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

public class AsyncCommonPool {

    public static void main(String[] args) throws Exception {
        RedisURI uri1 = RedisURI.Builder.redis("localhost", 6379).build();
        RedisURI uri2 = RedisURI.Builder.redis("localhost", 6380).build();
        RedisURI uri3 = RedisURI.Builder.redis("localhost", 6381).build();
        RedisURI uri4 = RedisURI.Builder.redis("localhost", 6382).build();
        RedisURI uri5 = RedisURI.Builder.redis("localhost", 6383).build();
        RedisURI uri6 = RedisURI.Builder.redis("localhost", 6384).build();
        RedisClusterClient redisClient = RedisClusterClient.create(Arrays.asList(uri1, uri2, uri3, uri4, uri5, uri6));
        //redisClient.getPartitions();
        GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool = ConnectionPoolSupport.createGenericObjectPool
                (() -> redisClient.connect(StringCodec.UTF8),
                        new GenericObjectPoolConfig<>());
        StatefulRedisClusterConnection<String, String> connection = pool.borrowObject();
        RedisAdvancedClusterAsyncCommands<String, String> commands = connection.async();
        CompletionStage<String> result = commands.set("key", "value")
                .thenCompose(s -> commands.get("key"))
                .whenComplete((r, t) -> {
                    connection.close();
                });

        result.toCompletableFuture().join();
        pool.close();
        redisClient.shutdown();
    }
}
