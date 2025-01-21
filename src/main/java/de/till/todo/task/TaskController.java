package de.till.todo.task;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/task")
@Validated
public class TaskController {

    private TaskService taskService;
    private TaskMapper taskMapper;

    @Autowired
    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping(path = "/alltasks")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        List<Task> tasks = taskService.getTasks();

        List<TaskResponseDTO> taskResponseDTOs = tasks.stream()
                .map(taskMapper::toTaskResponseDTO)
                .toList();

        return ResponseEntity.ok(taskResponseDTOs);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        Task createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.ok(taskMapper.toTaskResponseDTO(createdTask));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        if(task == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(taskMapper.toTaskResponseDTO(task));
        }
    }
}
