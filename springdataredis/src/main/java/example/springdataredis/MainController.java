package example.springdataredis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping
public class MainController {

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate;

    @Autowired
    @Qualifier("jedisRestTemplate")
    RedisTemplate<Object, Object> jedisRedisTemplate;

    @GetMapping("/get")
    public User get() {
        ValueOperations<Object, Object> valueOperations = redisTemplate.opsForValue();
        User user = new User("bar", 1);
        valueOperations.set("foo", user);
        Object foo = valueOperations.get("foo");
        ObjectMapper objectMapper = new ObjectMapper();
        User user1 = objectMapper.convertValue(foo, new TypeReference<User>() {});
        return user1;
    }

    @GetMapping("/get2")
    public Set<Object> get2() {
        ZSetOperations<Object, Object> zsetOperations = jedisRedisTemplate.opsForZSet();
        /*zsetOperations.add("sose", "car", 0.0);
        zsetOperations.add("sose", "bike", 1.0);*/
        zsetOperations.add("sose", new User("car", 1), 0.0);
        zsetOperations.add("sose", new User("bike", 2), 1.0);
        Set<Object> result = zsetOperations.range("sose", 0, -1);
        return result;
    }

    @GetMapping("/get3")
    public Mono<List<Object>> get3() {
        ReactiveHashOperations<Object, Object, Object> reactiveHashOperations = reactiveRedisTemplate.opsForHash();
        Map<String, String> hash = new HashMap<>();
        hash.put("name", "John");
        hash.put("surname", "Smith");
        hash.put("company", "Redis");
        hash.put("age", "29");
        String[] keys = new String[]{"name", "surname", "company", "age"};
        Mono<List<Object>> then = reactiveHashOperations.putAll("user-session:123", hash)
                .then(reactiveHashOperations.multiGet("user-session:123", Arrays.asList(keys))
                        .doOnNext(System.out::println));
        return then;
    }
}
