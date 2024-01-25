package org.gyro.todoapp.events;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TaskDeletedEvent implements Event {

    private String taskId;

}
