package example.lettuce;

import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.support.AsyncConnectionPoolSupport;
import io.lettuce.core.support.AsyncPool;
import io.lettuce.core.support.BoundedAsyncPool;
import io.lettuce.core.support.BoundedPoolConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class LettuceBoundedAsyncPool {
    public static void main(String[] args) {
        jedisClusterNodes.add(new HostAndPort("localhost", 6379));
        jedisClusterNodes.add(new HostAndPort("localhost", 6380));
        jedisClusterNodes.add(new HostAndPort("localhost", 6381));
        jedisClusterNodes.add(new HostAndPort("localhost", 6382));
        jedisClusterNodes.add(new HostAndPort("localhost", 6383));
        jedisClusterNodes.add(new HostAndPort("localhost", 6384));
        RedisClusterClient redisClient = RedisClusterClient.create(Arrays.asList(uri1, uri2, uri3, uri4, uri5, uri6));
        /**
         *
         You need to load partitions once before you can use asynchronous RedisClusterClient connect methods.
         This is, because partition refresh is a synchronous (blocking) process that needs to run before connecting to the cluster. Please call RedisClusterClient.getPartitions() after initializing RedisClusterClient. This call will fetch the partitions and optionally activate topology refresh. Afterwards, you're good to use async connect methods.
         */
        redisClient.getPartitions();
        CompletionStage<BoundedAsyncPool<StatefulRedisClusterConnection<String, String>>> poolFuture = AsyncConnectionPoolSupport.createBoundedObjectPoolAsync
                (() -> redisClient.connectAsync(StringCodec.UTF8),
                BoundedPoolConfig.create());
        AsyncPool<StatefulRedisClusterConnection<String, String>> pool = poolFuture.toCompletableFuture().join();
        CompletableFuture<Boolean> booleanCompletableFuture = pool.acquire().thenCompose(connection -> {
            RedisAdvancedClusterAsyncCommands<String, String> async = connection.async();
            connection.setAutoFlushCommands(false);
            //async.setAutoFlushCommands(false);
            List<RedisFuture<?>> futures = new ArrayList<>();
            futures.add(async.set("key", "value"));
            futures.add(async.set("key2", "value2"));
            //async.flushCommands();
            connection.flushCommands();
            boolean result = LettuceFutures.awaitAll(5, TimeUnit.SECONDS,
                    futures.toArray(new RedisFuture[futures.size()]));
            return CompletableFuture.completedFuture(result).whenComplete((s, throwable) -> {
                if (s) {
                    pool.release(connection);
                }
            });
        });
        booleanCompletableFuture.join();
        pool.closeAsync();
        redisClient.shutdown();
    }
}
