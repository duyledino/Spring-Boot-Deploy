package com.kimlongdev.shopme_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class ShopmeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopmeBackendApplication.class, args);
	}

    @Bean
    CommandLineRunner testRedisConnection(RedisConnectionFactory redisConnectionFactory,
                                          RedisTemplate<String, Object> redisTemplate) {
        return args -> {
            try {
                // Test ping
                String pong = redisConnectionFactory.getConnection().ping();
                System.out.println("✅ Kết nối Redis thành công!");
                System.out.println("Ping response: " + pong);

                // Test set/get
                redisTemplate.opsForValue().set("test-key", "Hello Redis!");
                String value = (String) redisTemplate.opsForValue().get("test-key");
                System.out.println("Test value: " + value);

                // Xóa test key
                redisTemplate.delete("test-key");

            } catch (Exception e) {
                System.err.println("❌ Kết nối Redis thất bại: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

}
