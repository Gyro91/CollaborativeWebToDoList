package org.gyro.todoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing
public class ToDoApp {

    public static void main(String[] args) {
        SpringApplication.run(ToDoApp.class, args);
    }

}
