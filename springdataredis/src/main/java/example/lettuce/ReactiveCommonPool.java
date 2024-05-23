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
        RedisURI uri1 = RedisURI.Builder.redis("localhost", 7000).build();
        RedisURI uri2 = RedisURI.Builder.redis("localhost", 7001).build();
        RedisURI uri3 = RedisURI.Builder.redis("localhost", 7002).build();
        RedisURI uri4 = RedisURI.Builder.redis("localhost", 7010).build();
        RedisURI uri5 = RedisURI.Builder.redis("localhost", 7011).build();
        RedisURI uri6 = RedisURI.Builder.redis("localhost", 7012).build();
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
