package org.gyro.todoapp.service;

import com.mongodb.client.model.changestream.OperationType;
import lombok.RequiredArgsConstructor;
import org.gyro.todoapp.events.Event;
import org.gyro.todoapp.exceptions.TaskNotFoundException;
import org.gyro.todoapp.exceptions.TaskVersionException;
import org.gyro.todoapp.mapper.TaskMapper;
import org.gyro.todoapp.model.*;
import org.gyro.todoapp.repository.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TaskService {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.by("lastModifiedDate"));
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<TaskResource> create(final NewTaskItem item) {
        return taskRepository.save(taskMapper.toModel(item))
                .map(taskMapper::toResource);
    }

    public Flux<TaskResource> findAll() {
        return taskRepository.findAll(DEFAULT_SORT)
                .map(taskMapper::toResource);
    }

    public Mono<TaskResource> findById(final String id, final Long version) {
        return findTaskById(id, version)
                .map(taskMapper::toResource);
    }

    public Mono<TaskResource> update(final String id, final Long version, final TaskUpdateResource taskUpdateResource) {
        return findTaskById(id, version)
                .flatMap(task -> {
                    taskMapper.update(taskUpdateResource, task);
                    return taskRepository.save(task);
                })
                .map(taskMapper::toResource);
    }

    public Mono<TaskResource> patch(final String id, final Long version, final TaskPatchResource taskPatchResource) {
        return findTaskById(id, version)
                .flatMap(task -> {
                    if (taskPatchResource.getDescription() != null) {
                        task.setDescription(taskPatchResource.getDescription().get());
                    }
                    if (taskPatchResource.getStatus() != null) {
                        task.setStatus(taskPatchResource.getStatus().get());
                    }
                    return taskRepository.save(task);
                })
                .map(taskMapper::toResource);
    }

    public Mono<Void> deleteById(final String id, final Long version) {
        return findTaskById(id, version)
                .flatMap(taskRepository::delete);
    }

    private Mono<Task> findTaskById(final String id, final Long expectedVersion) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException(id)))
                .flatMap(task -> {
                    if (expectedVersion != null && !task.getVersion().equals(expectedVersion)) {
                        return Mono.error(new TaskVersionException(String.format("Task version %d not as expected %d", expectedVersion, task.getVersion())));
                    }
                    return Mono.just(task);
                });
    }

    public Flux<Event> listenToEvents() {
        final ChangeStreamOptions changeStreamOptions = ChangeStreamOptions.builder()
                .returnFullDocumentOnUpdate()
                .filter(Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("operationType")
                                .in(OperationType.INSERT.getValue(),
                                        OperationType.REPLACE.getValue(),
                                        OperationType.UPDATE.getValue(),
                                        OperationType.DELETE.getValue()))))
                .build();

        return reactiveMongoTemplate.changeStream("task", changeStreamOptions, Task.class)
                .map(taskMapper::toEvent);
    }
}
