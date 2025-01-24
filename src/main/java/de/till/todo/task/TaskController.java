package de.till.todo.task;

import de.till.todo.user.User;
import de.till.todo.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

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
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(NOT_FOUND).build();
        }

        Task newTask = taskDTO.toTask();
        newTask.setUser(currentUser);

        Task createdTask = taskService.createTask(newTask);

        return ResponseEntity.ok(taskMapper.toTaskResponseDTO(createdTask));
    }

    @GetMapping(path = "/my-tasks")
    public ResponseEntity<List<TaskResponseDTO>> getUserTasks() {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return ResponseEntity.status(NOT_FOUND).build();
        }

        List<Task> tasks = taskService.getTaskByUser(currentUser);

        List<TaskResponseDTO> taskResponseDTOS = tasks.stream()
                .map(taskMapper::toTaskResponseDTO)
                .toList();

        return ResponseEntity.ok(taskResponseDTOS);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id);
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
