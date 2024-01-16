package org.gyro.todoapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TaskUpdateResource {

    @NotBlank
    private String description;

    @NotNull
    private TaskStatus status;

}