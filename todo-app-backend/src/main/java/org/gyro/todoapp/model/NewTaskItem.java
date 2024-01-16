package org.gyro.todoapp.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NewTaskItem {

    @NotBlank
    private String description;

}