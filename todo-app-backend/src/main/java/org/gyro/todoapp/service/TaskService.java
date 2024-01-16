package org.gyro.todoapp.service;


import lombok.RequiredArgsConstructor;
import org.gyro.todoapp.exceptions.TaskNotFoundException;
import org.gyro.todoapp.mapper.TaskMapper;
import org.gyro.todoapp.model.*;
import org.gyro.todoapp.repository.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.by("lastModifiedDate"));

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public Mono<TaskResource> create(final NewTaskItem item) {

        return taskRepository.save(taskMapper.toModel(item))
                .map(taskMapper::toResource);
    }

    public Flux<TaskResource> findAll() {
        return taskRepository.findAll(DEFAULT_SORT)
                .map(taskMapper::toResource);
    }

    public Mono<TaskResource> findById(final String id) {

        return findItemById(id)
                .map(taskMapper::toResource);
    }

    public Mono<TaskResource> update(final String id, final TaskUpdateResource itemUpdateResource) {

        return findItemById(id)
                .flatMap(item -> {
                    taskMapper.update(itemUpdateResource, item);
                    return taskRepository.save(item);
                })
                .map(taskMapper::toResource);
    }

    @SuppressWarnings({"OptionalAssignedToNull", "OptionalGetWithoutIsPresent"})
    public Mono<TaskResource> patch(final String id, final TaskPatchResource itemPatchResource) {

        return findItemById(id)
                .flatMap(item -> {
                    if (itemPatchResource.getDescription() != null) {
                        // The description has been provided in the patch
                        item.setDescription(itemPatchResource.getDescription().get());
                    }

                    if (itemPatchResource.getStatus() != null) {
                        // The status has been provided in the patch
                        item.setStatus(itemPatchResource.getStatus().get());
                    }
                    return taskRepository.save(item);
                })
                .map(taskMapper::toResource);
    }

    public Mono<Void> deleteById(final String id) {

        return findItemById(id)
                .flatMap(taskRepository::delete);
    }

    private Mono<Task> findItemById(final String id) {

        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException(id)));
    }

}