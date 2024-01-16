package org.gyro.todoapp.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class TaskResource {

    private String id;
    private Long version;

    private String description;
    private TaskStatus status;

    private Instant createdDate;
    private Instant lastModifiedDate;

}