package org.gyro.todoapp.web;

import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gyro.todoapp.events.Event;
import org.gyro.todoapp.exceptions.TaskNotFoundException;
import org.gyro.todoapp.exceptions.TaskVersionException;
import org.gyro.todoapp.model.NewTaskItem;
import org.gyro.todoapp.model.TaskPatchResource;
import org.gyro.todoapp.model.TaskResource;
import org.gyro.todoapp.model.TaskUpdateResource;
import org.gyro.todoapp.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.http.HttpHeaders.IF_MATCH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/tasks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    private final TaskService taskService;

    @ApiOperation("Get the list of items")
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
    public Mono<ResponseEntity<TaskResource>> updateTask(@PathVariable @NotNull final String id,
                                                         @Valid @RequestBody TaskUpdateResource taskUpdateResource,
                                                         @RequestHeader(name = IF_MATCH, required = false) Long version) {
        return taskService.update(id, version, taskUpdateResource)
                .map(ResponseEntity::ok)
                .onErrorResume(TaskNotFoundException.class, e ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage())))
                .onErrorResume(TaskVersionException.class, e ->
                        Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage())));
    }

    @ApiOperation("Patch an existing item following the patch merge RFC (https://tools.ietf.org/html/rfc7386)")
    @PatchMapping(value = "/{id}")
    public Mono<ResponseEntity<TaskResource>> patchTask(@PathVariable @NotNull final String id,
                                                        @Valid @RequestBody TaskPatchResource taskPatchResource,
                                                        @RequestHeader(name = IF_MATCH, required = false) Long version) {
        return taskService.patch(id, version, taskPatchResource)
                .map(ResponseEntity::ok)
                .onErrorResume(TaskNotFoundException.class, e ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage())))
                .onErrorResume(TaskVersionException.class, e ->
                        Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage())));
    }

    @ApiOperation("Find an item by its id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public Mono<TaskResource> findById(@PathVariable String id,
                                       @RequestHeader(name = IF_MATCH, required = false) Long version) {
        return taskService.findById(id, version);
    }

    @ApiOperation("Delete an item")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable final String id,
                                             @RequestHeader(name = IF_MATCH, required = false) Long version) {
        return taskService.deleteById(id, version)
                .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                .onErrorResume(TaskNotFoundException.class, e ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage())))
                .onErrorResume(TaskVersionException.class, e ->
                        Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage())));
    }

    @ApiOperation("Get the item event stream")
    @GetMapping(value = "events")
    public Flux<ServerSentEvent<Event>> getEventStream() {

        return taskService.listenToEvents()
                .map(event -> ServerSentEvent.<Event>builder()
                        .retry(Duration.ofSeconds(5))
                        .event(event.getClass().getSimpleName())
                        .data(event).build());
    }
}
