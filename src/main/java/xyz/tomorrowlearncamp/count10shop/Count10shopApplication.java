package xyz.tomorrowlearncamp.count10shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
public class Count10shopApplication {

    public static void main(String[] args) {
        SpringApplication.run(Count10shopApplication.class, args);
    }

}
