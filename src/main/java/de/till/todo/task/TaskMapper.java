package de.till.todo.task;

import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponseDTO toTaskResponseDTO(Task task) {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setId(task.getId());
        taskResponseDTO.setDescription(task.getDescription());
        taskResponseDTO.setPriority(task.getPriority());
        taskResponseDTO.setTitle(task.getTitle());
        taskResponseDTO.setUserId(task.getUser().getId());
        return taskResponseDTO;
    }
}
