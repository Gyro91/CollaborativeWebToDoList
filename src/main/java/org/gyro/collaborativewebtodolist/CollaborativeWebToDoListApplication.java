package org.gyro.collaborativewebtodolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing
public class CollaborativeWebToDoListApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollaborativeWebToDoListApplication.class, args);
    }

}
