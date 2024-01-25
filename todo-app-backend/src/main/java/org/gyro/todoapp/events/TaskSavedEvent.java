package org.gyro.todoapp.events;

import lombok.Data;
import lombok.experimental.Accessors;
import org.gyro.todoapp.model.TaskResource;

@Data
@Accessors(chain = true)
public class TaskSavedEvent implements Event {

    private TaskResource taskResource;

}
