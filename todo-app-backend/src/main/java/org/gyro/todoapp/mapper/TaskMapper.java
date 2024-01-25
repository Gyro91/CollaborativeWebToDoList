package org.gyro.todoapp.mapper;

import org.bson.BsonObjectId;
import org.gyro.todoapp.events.Event;
import org.gyro.todoapp.events.TaskDeletedEvent;
import org.gyro.todoapp.events.TaskSavedEvent;
import org.gyro.todoapp.model.NewTaskItem;
import org.gyro.todoapp.model.Task;
import org.gyro.todoapp.model.TaskResource;
import org.gyro.todoapp.model.TaskUpdateResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.mongodb.core.ChangeStreamEvent;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskResource toResource(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    Task toModel(NewTaskItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    void update(TaskUpdateResource updateResource, @MappingTarget Task task);

    default Event toEvent(final ChangeStreamEvent<Task> changeStreamEvent) {

        final Event event;

        switch (changeStreamEvent.getOperationType()) {
            case DELETE:
                // In case of deletion, the body is not set so we need to extract the objectId from the raw document
                event = new TaskDeletedEvent().setTaskId(((BsonObjectId) changeStreamEvent.getRaw()
                        .getDocumentKey().get("_id")).getValue().toString());
                break;
            case INSERT:
            case UPDATE:
            case REPLACE:
                // Item saved
                event = new TaskSavedEvent().setTaskResource(toResource(changeStreamEvent.getBody()));
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("The Mongo operation type [%s] is not supported", changeStreamEvent.getOperationType()));
        }

        return event;
    }
}
