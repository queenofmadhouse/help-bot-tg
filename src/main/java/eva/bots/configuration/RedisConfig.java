package eva.bots.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

    @Bean
    public Jedis jedis(@Value("${spring.redis.host}") String host,
                       @Value("${spring.redis.port}") int port) {

        return new Jedis(host, port);
    }
}
