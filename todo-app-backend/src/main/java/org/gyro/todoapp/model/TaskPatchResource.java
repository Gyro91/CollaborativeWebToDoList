package org.gyro.todoapp.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Optional;

@Data
@Accessors(chain = true)
public class TaskPatchResource {

    private Optional<@NotBlank String> description;
    private Optional<@NotNull TaskStatus> status;

}