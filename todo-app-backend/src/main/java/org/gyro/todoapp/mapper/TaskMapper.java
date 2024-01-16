package org.gyro.todoapp.mapper;

import org.gyro.todoapp.model.NewTaskItem;
import org.gyro.todoapp.model.Task;
import org.gyro.todoapp.model.TaskResource;
import org.gyro.todoapp.model.TaskUpdateResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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

}
