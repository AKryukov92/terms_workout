package ru.ominit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

/**
 * Created by Александр on 29.03.2018.
 */
@SpringBootApplication
public class Application {

    @Bean
    public Random getRandomSource(){
        return new Random();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
