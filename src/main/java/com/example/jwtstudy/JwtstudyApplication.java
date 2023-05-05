package com.example.jwtstudy;

import com.example.jwtstudy.model.user.User;
import com.example.jwtstudy.model.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JwtstudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtstudyApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return (args -> {
            userRepository.save(User.builder().username("taeheoki").password("1234").email("taeheoki@naver.com").role("USER").build());
            userRepository.save(User.builder().username("admin").password("1234").email("admin@naver.com").role("ADMIN").build());
        });
    }
}
