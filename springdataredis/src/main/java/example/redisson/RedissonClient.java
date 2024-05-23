package example.redisson;

import org.redisson.Redisson;
import org.redisson.config.Config;

public class RedissonClient {

    public static void main(String[] args) {
        Config config = new Config();
        config.useClusterServers()
                //.setPassword("bitnami")
                .addNodeAddress("redis://localhost:6379")
                .addNodeAddress("redis://localhost:6380")
                .addNodeAddress("redis://localhost:6381")
                .addNodeAddress("redis://localhost:6382")
                .addNodeAddress("redis://localhost:6383")
                .addNodeAddress("redis://localhost:6384");
        org.redisson.api.RedissonClient redissonClient = Redisson.create(config);
        redissonClient.getBucket("test_redis").set("foo");
        Object test_redis = redissonClient.getBucket("test_redis").get();
        System.out.println(test_redis);
        redissonClient.shutdown();

    }
}
