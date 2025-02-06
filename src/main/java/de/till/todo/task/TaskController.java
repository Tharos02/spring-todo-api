package de.till.todo.task;

import de.till.todo.user.User;
import de.till.todo.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping(path = "/task")
@Validated
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, TaskMapper taskMapper, UserService userService) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.userService = userService;
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
        TaskResponseDTO createdTaskDTO = taskService.createTask(taskDTO);
        return ResponseEntity.ok(createdTaskDTO);
    }

    @PatchMapping(path = "/update/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@Valid @RequestBody TaskDTO taskDTO, @PathVariable Long id) {
        TaskResponseDTO updatedTaskDTO = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updatedTaskDTO);
    }

    @GetMapping(path = "/my-tasks")
    public ResponseEntity<List<TaskResponseDTO>> getUserTasks() {
        List<TaskResponseDTO> tasks = taskService.getTaskForCurrentUser();
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(NOT_FOUND).build(); // Task nicht gefunden
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(UNAUTHORIZED).build(); // Unauthorized
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(taskMapper.toTaskResponseDTO(task));
        }
    }
}
