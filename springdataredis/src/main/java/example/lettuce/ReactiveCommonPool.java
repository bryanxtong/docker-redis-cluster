package example.lettuce;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import reactor.core.publisher.Mono;

import java.util.Arrays;

public class ReactiveCommonPool {

    public static void main(String[] args) throws Exception {
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
        StatefulRedisClusterConnection<String, String> connection = pool.borrowObject();
        RedisAdvancedClusterReactiveCommands<String, String> reactive = connection.reactive();
        Mono<String> stringMono = reactive.set("key", "value")
                .then(reactive.get("key").doOnNext(System.out::println))
                .doOnTerminate(() -> {
                    connection.close();
                    /* pool.close();
                    redisClient.shutdown();*/
                });
        stringMono.block();
        pool.close();
        redisClient.shutdown();
        //Thread.currentThread().join();
    }
}
