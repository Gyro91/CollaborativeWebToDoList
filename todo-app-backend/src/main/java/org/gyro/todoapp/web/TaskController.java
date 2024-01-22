package org.gyro.todoapp.web;


import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gyro.todoapp.exceptions.TaskNotFoundException;
import org.gyro.todoapp.model.NewTaskItem;
import org.gyro.todoapp.model.TaskPatchResource;
import org.gyro.todoapp.model.TaskResource;
import org.gyro.todoapp.model.TaskUpdateResource;
import org.gyro.todoapp.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/tasks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    private final TaskService taskService;

    @ApiOperation("Get a the list of items")
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TaskResource> getAllItems() {

        return taskService.findAll();
    }

    @ApiOperation("Create a new item")
    @PostMapping
    public Mono<TaskResource> create(@Valid @RequestBody final NewTaskItem item) {

        return taskService.create(item);
    }

    @ApiOperation("Update an existing item")
    @PutMapping(value = "/{id}")
    public Mono<TaskResource> update(@PathVariable @NotNull final String id,
                                     @Valid @RequestBody TaskUpdateResource taskUpdateResource) {

        return taskService.update(id, taskUpdateResource);
    }

    @ApiOperation("Patch an existing item following the patch merge RCF (https://tools.ietf.org/html/rfc7386)")
    @PatchMapping(value = "/{id}")
    public Mono<TaskResource> update(@PathVariable @NotNull final String id,
                                     @Valid @RequestBody TaskPatchResource taskPatchResource) {

        return taskService.patch(id, taskPatchResource);
    }


    @ApiOperation("Find an item by its id")
    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    public Mono<TaskResource> findById(@PathVariable String id) {

        return taskService.findById(id);
    }

    @ApiOperation("Delete an item")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<TaskResource>> delete(@PathVariable final String id) {
        return taskService.findById(id)
                .flatMap(task -> taskService.deleteById(id)
                        .thenReturn(ResponseEntity.ok(task)))
                .onErrorResume(TaskNotFoundException.class, e ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage())));
    }


}

