package org.gyro.collaborativewebtodolist.repository;

import org.gyro.collaborativewebtodolist.model.Task;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
}